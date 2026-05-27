package com.webquiz.domain.service.impl;

import com.webquiz.contact.validate.QuestionValidator;
import com.webquiz.domain.entity.ExamQuestion;
import com.webquiz.domain.entity.OptionBank;
import com.webquiz.domain.entity.QuestionBank;
import com.webquiz.domain.repository.ExamQuestionRepository;
import com.webquiz.domain.repository.ExamRepository;
import com.webquiz.domain.repository.QuestionBankRepository;
import com.webquiz.domain.service.ExamQuestionService;
import com.webquiz.domain.service.QuestionBankService;
import com.webquiz.web.dto.request.examQuestion.ExamOptionDto;
import com.webquiz.web.dto.request.examQuestion.ExamQuestionRequest;
import com.webquiz.web.dto.request.examQuestion.ImportQuestionBankRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class ExamQuestionServiceImpl implements ExamQuestionService {

    private final ExamQuestionRepository examQuestionRepository;
    private final QuestionBankRepository questionBankRepository;
    private final ExamRepository examRepository;

    @Override
    @Transactional
    public void upsertExamQuestion(String examId, List<ExamQuestionRequest> requests) {

        examRepository.findById(examId)
                      .orElseThrow(() -> new RuntimeException("Exam không tồn tại"));

        for (ExamQuestionRequest item : requests) {

            QuestionValidator.validate(item.getType(), item.getOptions(), item.getCorrectAnswer(),
                                       item.getCorrectAnswerKeys());
            if (item.getId() == null) {
                ExamQuestion newQuestion = ExamQuestion.builder()
                                                       .examId(examId)
                                                       .content(item.getContent())
                                                       .type(item.getType())
                                                       .options(mapOptions(item))
                                                       .correctAnswer(item.getCorrectAnswer())
                                                       .correctAnswerKeys(item.getCorrectAnswerKeys())
                                                       .level(item.getLevel())
                                                       .build();

                examQuestionRepository.save(newQuestion);

            } else {
                ExamQuestion existing = examQuestionRepository.findById(item.getId())
                                                              .orElseThrow(() -> new RuntimeException("Question không tồn tại: " + item.getId()));

                if (!existing.getExamId().equals(examId)) {
                    throw new RuntimeException("Question không thuộc exam này: " + item.getId());
                }

                existing.setContent(item.getContent());
                existing.setType(item.getType());
                existing.setOptions(mapOptions(item));
                existing.setCorrectAnswer(item.getCorrectAnswer());
                existing.setCorrectAnswerKeys(item.getCorrectAnswerKeys());
                existing.setLevel(item.getLevel());

                examQuestionRepository.save(existing);
            }
        }

        syncTotalQuestions(examId);
    }

    @Override
    public List<ExamQuestionRequest> previewFromBank(ImportQuestionBankRequest request) {

        List<QuestionBank> banks =
                questionBankRepository.findAllById(request.getQuestionBankIds());

        if (banks.isEmpty()) {
            throw new RuntimeException("Không tìm thấy câu hỏi");
        }

        return banks.stream().map(bank -> {
            ExamQuestionRequest req = new ExamQuestionRequest();

            req.setContent(bank.getContent());
            req.setType(bank.getType());
            req.setOptions(mapOptionsToDto(bank.getOptions()));
            req.setCorrectAnswer(bank.getCorrectAnswer());
            req.setCorrectAnswerKeys(bank.getCorrectAnswerKeys());
            req.setLevel(bank.getLevel());

            return req;
        }).toList();
    }

    @Override
    @Transactional
    public void delete(String questionId) {
        ExamQuestion question = examQuestionRepository.findById(questionId)
                                                      .orElseThrow(() -> new RuntimeException("Question không tồn tại: " + questionId));

        examQuestionRepository.delete(question);
        syncTotalQuestions(question.getExamId());
    }


    private void syncTotalQuestions(String examId) {
        examRepository.findById(examId).ifPresent(exam -> {
            long count = examQuestionRepository.countByExamId(examId);
            exam.setTotalQuestions((int) count);
            examRepository.save(exam);
        });
    }

    private List<OptionBank> mapOptions(ExamQuestionRequest request) {
        if (request.getOptions() == null) return null;
        return request.getOptions().stream()
                      .map(o -> OptionBank.builder()
                                          .key(o.getKey())
                                          .text(o.getText())
                                          .build())
                      .toList();
    }

    private List<ExamOptionDto> mapOptionsToDto(List<OptionBank> options) {

        if (options == null) return null;

        return options.stream()
                      .map(o -> {
                          ExamOptionDto dto = new ExamOptionDto();
                          dto.setKey(o.getKey());
                          dto.setText(o.getText());
                          return dto;
                      })
                      .toList();
    }
}
