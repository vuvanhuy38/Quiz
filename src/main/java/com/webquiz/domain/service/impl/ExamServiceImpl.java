package com.webquiz.domain.service.impl;

import com.webquiz.contact.SessionUtil;
import com.webquiz.contact.enums.StatusExamType;
import com.webquiz.domain.entity.Exam;
import com.webquiz.domain.repository.ExamRepository;
import com.webquiz.domain.service.ExamService;
import com.webquiz.web.dto.request.exam.CreateExamRequest;
import com.webquiz.web.dto.request.exam.UpdateExamRequest;
import com.webquiz.web.dto.response.exam.ExamItemResponse;
import com.webquiz.web.dto.response.exam.ExamResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;

@Service
@AllArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;

    @Override
    @Transactional
    public void create(CreateExamRequest examRequest) {

        String createdBy = SessionUtil.getCurrentUser().getUsername();

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
    public ExamResponse getHome() {
        List<Exam> featured = examRepository
                .findTop10ByStatusOrderByAttemptCountDesc(StatusExamType.PUBLIC);

        List<Exam> latest = examRepository
                .findTop10ByStatusOrderByCreatedAtDesc(StatusExamType.PUBLIC);

        return ExamResponse.builder()
                           .featuredExams(mapToExamItems(featured))
                           .latestExams(mapToExamItems(latest))
                           .build();
    }

    @Override
    public List<ExamItemResponse> search(String title) {

        String normalizedtitle = Normalizer.normalize(title, Normalizer.Form.NFC);

        List<Exam> exams = examRepository.findByStatusAndTitleContainingIgnoreCase(StatusExamType.PUBLIC, normalizedtitle);

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
}
