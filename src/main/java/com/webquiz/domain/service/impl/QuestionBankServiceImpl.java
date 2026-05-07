package com.webquiz.domain.service.impl;

import com.webquiz.contact.validate.QuestionValidator;
import com.webquiz.domain.entity.OptionBank;
import com.webquiz.domain.entity.QuestionBank;
import com.webquiz.domain.repository.QuestionBankRepository;
import com.webquiz.domain.service.QuestionBankService;
import com.webquiz.web.dto.request.questionBank.CreateQuestionRequest;
import com.webquiz.web.dto.request.questionBank.OptionBankDto;
import com.webquiz.web.dto.request.questionBank.UpdateQuestionRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class QuestionBankServiceImpl implements QuestionBankService {

    private final QuestionBankRepository questionBankRepository;

    @Override
    @Transactional
    public void create(CreateQuestionRequest request) {
        QuestionValidator.validate(request.getType(), request.getOptions(), request.getCorrectAnswer(),
                                   request.getCorrectAnswerKeys());

        QuestionBank question = QuestionBank.builder()
                                            .content(request.getContent())
                                            .categoryId(request.getCategoryId())
                                            .type(request.getType())
                                            .options(mapOptions(request.getOptions()))
                                            .correctAnswer(request.getCorrectAnswer())
                                            .correctAnswerKeys(request.getCorrectAnswerKeys())
                                            .level(request.getLevel())
                                            .build();

        questionBankRepository.save(question);
    }

    @Override
    @Transactional
    public void update(String id, UpdateQuestionRequest request) {
        QuestionValidator.validate(request.getType(), request.getOptions(), request.getCorrectAnswer(),
                                   request.getCorrectAnswerKeys());

        QuestionBank question = questionBankRepository.findById(id)
                                                  .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi với id: " + id));

        question.setContent(request.getContent());
        question.setCategoryId(request.getCategoryId());
        question.setType(request.getType());
        question.setOptions(mapOptions(request.getOptions()));
        question.setCorrectAnswer(request.getCorrectAnswer());
        question.setCorrectAnswerKeys(request.getCorrectAnswerKeys());
        question.setLevel(request.getLevel());

        questionBankRepository.save(question);
    }

    private List<OptionBank> mapOptions(List<OptionBankDto> options) {
        if (options == null) return null;

        return options.stream()
                      .map(o -> OptionBank.builder()
                                          .key(o.getKey())
                                          .text(o.getText())
                                          .build())
                      .toList();
    }
}
