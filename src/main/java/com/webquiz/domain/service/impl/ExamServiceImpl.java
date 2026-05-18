package com.webquiz.domain.service.impl;

import com.webquiz.contact.SessionUtil;
import com.webquiz.contact.enums.StatusExamType;
import com.webquiz.domain.entity.Exam;
import com.webquiz.domain.entity.ExamQuestion;
import com.webquiz.domain.entity.OptionBank;
import com.webquiz.domain.repository.ExamQuestionRepository;
import com.webquiz.domain.repository.ExamRepository;
import com.webquiz.domain.service.ExamService;
import com.webquiz.web.dto.common.ResponsePage;
import com.webquiz.web.dto.request.exam.CreateExamRequest;
import com.webquiz.web.dto.request.exam.UpdateExamRequest;
import com.webquiz.web.dto.response.exam.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;

@Service
@AllArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;

    @Override
    @Transactional
    public void create(CreateExamRequest examRequest) {

        String createdBy = SessionUtil.getCurrentUser().getId();

        Exam exam = Exam.builder()
                        .title(examRequest.getTitle())
                        .description(examRequest.getDescription())
                        .timeLimit(examRequest.getTimeLimit())
                        .status(examRequest.getStatus())
                        .createdBy(createdBy)
                        .build();

        examRepository.save(exam);
    }

    @Override
    public void update(String id, UpdateExamRequest updateExamRequeste) {
        Exam exam = examRepository.findById(id)
                                  .orElseThrow(() -> new RuntimeException("Exam not found with id: " + id));

        exam.setTitle(updateExamRequeste.getTitle());
        exam.setDescription(updateExamRequeste.getDescription());
        exam.setTimeLimit(updateExamRequeste.getTimeLimit());
        exam.setStatus(updateExamRequeste.getStatus());

        examRepository.save(exam);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Exam exam = examRepository.findById(id)
                                  .orElseThrow(() -> new RuntimeException("Exam not found with id: " + id));

        examQuestionRepository.deleteAllByExamId(id);
        examRepository.delete(exam);
    }

    @Override
    public ExamResponse getHome() {
        List<Exam> featured = examRepository
                .findTop10ByStatusOrderByAttemptCountDesc(StatusExamType.INACTIVE);

        List<Exam> latest = examRepository
                .findTop10ByStatusOrderByCreatedAtDesc(StatusExamType.ACTIVE);

        return ExamResponse.builder()
                           .featuredExams(mapToExamItems(featured))
                           .latestExams(mapToExamItems(latest))
                           .build();
    }

    @Override
    public List<ExamItemResponse> search(String title) {

        String normalizedtitle = Normalizer.normalize(title, Normalizer.Form.NFC);

        List<Exam> exams = examRepository.findByStatusAndTitleContainingIgnoreCase(StatusExamType.ACTIVE, normalizedtitle);

        return exams.stream()
                    .map(exam -> ExamItemResponse.builder()
                                             .id(exam.getId())
                                             .title(exam.getTitle())
                                             .description(exam.getDescription())
                                             .timeLimit(exam.getTimeLimit())
                                             .totalQuestions(exam.getTotalQuestions())
                                             .attemptCount(exam.getAttemptCount())
                                             .build())
                    .toList();
    }

    @Override
    public ExamDetailResponse getExamDetail(String id) {

        Exam exam = examRepository.findById(id)
                                  .orElseThrow(() ->  new RuntimeException("ko tìm thấy đề thi"));

        List<ExamQuestion> questions =
                examQuestionRepository.findByExamId(exam.getId());

        List<ExamQuestionItemDto> questionResponses = questions.stream()
                                                               .map(question -> ExamQuestionItemDto.builder()
                                                                                                     .id(question.getId())
                                                                                                     .content(question.getContent())
                                                                                                     .type(question.getType())
                                                                                                     .options(mapToOptionDtos(question.getOptions()))
                                                                                                     .correctAnswer(question.getCorrectAnswer())
                                                                                                     .correctAnswerKeys(question.getCorrectAnswerKeys())
                                                                                                     .level(question.getLevel())
                                                                                                     .build())
                                                               .toList();

        return ExamDetailResponse.builder()
                                 .id(exam.getId())
                                 .title(exam.getTitle())
                                 .description(exam.getDescription())
                                 .categoryId(exam.getCategoryId())
                                 .timeLimit(exam.getTimeLimit())
                                 .totalQuestions(exam.getTotalQuestions())
                                 .attemptCount(exam.getAttemptCount())
                                 .questions(questionResponses)
                                 .build();
    }

    @Override
    public ResponsePage<List<ExamItemResponse>> getAllExams(Pageable pageable, String keyword,
                                                            String categoryId, StatusExamType status) {

        Page<Exam> examPage = examRepository.findAllWithFilters(keyword, categoryId, status, pageable);

        List<ExamItemResponse> items = mapToExamItems(examPage.getContent());

        return ResponsePage.<List<ExamItemResponse>>builder()
                           .data(items)
                           .totalElement(examPage.getTotalElements())
                           .totalPage(examPage.getTotalPages())
                           .pageSize(examPage.getSize())
                           .pageIndex(examPage.getNumber())
                           .build();
    }

    private List<ExamItemResponse> mapToExamItems(List<Exam> exams) {
        return exams.stream()
                    .map(exam -> ExamItemResponse.builder()
                                                 .id(exam.getId())
                                                 .title(exam.getTitle())
                                                 .description(exam.getDescription())
                                                 .timeLimit(exam.getTimeLimit())
                                                 .totalQuestions(exam.getTotalQuestions())
                                                 .attemptCount(exam.getAttemptCount())
                                                 .createdAt(exam.getCreatedAt())
                                                 .build())
                    .toList();
    }

    private List<ExamOptionBankDto> mapToOptionDtos(List<OptionBank> options) {

        return options.stream()
                      .map(option -> ExamOptionBankDto.builder()
                                                      .key(option.getKey())
                                                      .text(option.getText())
                                                      .build())
                      .toList();
    }
}
