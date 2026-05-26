package com.webquiz.domain.service.impl;

import com.webquiz.contact.SessionUtil;
import com.webquiz.contact.enums.AttemptStatusType;
import com.webquiz.contact.enums.StatusExamType;
import com.webquiz.domain.entity.*;
import com.webquiz.domain.repository.ExamAttemptRepository;
import com.webquiz.domain.repository.ExamQuestionRepository;
import com.webquiz.domain.repository.ExamRepository;
import com.webquiz.domain.service.ExamAttemptService;
import com.webquiz.security.CustomUserDetails;
import com.webquiz.web.dto.common.ResponsePage;
import com.webquiz.web.dto.request.attempt.SubmitAnswerItemDto;
import com.webquiz.web.dto.request.attempt.SubmitExamRequest;
import com.webquiz.web.dto.response.attempt.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ExamAttemptServiceImpl implements ExamAttemptService {

    private final ExamAttemptRepository attemptRepository;
    private final ExamQuestionRepository questionRepository;
    private final ExamRepository examRepository;

    @Override
    public StartAttemptResponse startAttempt(String examId) {

        List<ExamQuestion> questions = questionRepository.findByExamId(examId);

        Exam exam = examRepository.findById(examId)
                                  .orElseThrow(() -> new RuntimeException("Không tìm thấy đề thi"));

        CustomUserDetails user = SessionUtil.getCurrentUser();

        ExamAttempt attempt = ExamAttempt.builder()
                                         .examId(exam.getId())
                                         .userId(user.getId())
                                         .startedAt(LocalDateTime.now())
                                         .status(AttemptStatusType.IN_PROGRESS)
                                         .totalQuestions(questions.size())
                                         .build();

        ExamAttempt saved = attemptRepository.save(attempt);

        return StartAttemptResponse.builder()
                                   .attemptId(saved.getId())
                                   .startedAt(saved.getStartedAt())
                                   .timeLimit(exam.getTimeLimit())
                                   .build();
    }

    @Override
    public List<ExamQuestionResponse> getQuestions(String attemptId) {

        ExamAttempt attempt = attemptRepository.findById(attemptId)
                                               .orElseThrow(() -> new RuntimeException("Attempt not found"));

        List<ExamQuestion> questions = questionRepository.findByExamId(attempt.getExamId());

        return questions.stream()
                        .map(q -> ExamQuestionResponse.builder()
                                                      .questionId(q.getId())
                                                      .content(q.getContent())
                                                      .type(q.getType().name())
                                                      .options(convertOptions(q.getOptions()))
                                                      .build())
                        .toList();
    }

    @Override
    public SubmitExamResponse submitExam(String attemptId, SubmitExamRequest request) {

        ExamAttempt attempt = attemptRepository.findById(attemptId)
                                               .orElseThrow(() -> new RuntimeException("Attempt not found"));

        List<Answers> answerList = new ArrayList<>();
        long correctCount = 0;

        for (SubmitAnswerItemDto item : request.getAnswers()) {

            ExamQuestion question = questionRepository.findById(item.getExamQuestionId())
                                                      .orElseThrow(() -> new RuntimeException("Question not found"));

            boolean isCorrect = switch (question.getType()) {
                case SINGLE_CHOICE, TRUE_FALSE ->
                        question.getCorrectAnswer() != null
                        && question.getCorrectAnswer().equalsIgnoreCase(item.getSelectedAnswer());

                case MULTIPLE_CHOICE ->
                        item.getSelectedKeys() != null
                        && question.getCorrectAnswerKeys() != null
                        && new HashSet<>(item.getSelectedKeys())
                                .equals(new HashSet<>(question.getCorrectAnswerKeys()));
            };

            if (isCorrect) correctCount++;

            answerList.add(Answers.builder()
                                  .examQuestionId(item.getExamQuestionId())
                                  .selectedAnswer(item.getSelectedAnswer())
                                  .selectedKeys(item.getSelectedKeys())
                                  .isCorrect(isCorrect)
                                  .pointsEarned(isCorrect ? 1 : 0)
                                  .build());
        }

        double score = Math.round((((double) correctCount / attempt.getTotalQuestions()) * 10) * 100) / 100.0;

        attempt.setAnswers(answerList);
        attempt.setScore(score);
        attempt.setFinishedAt(LocalDateTime.now());
        attempt.setStatus(AttemptStatusType.COMPLETED);
        attemptRepository.save(attempt);

        // tăng lượt làm bài
        Exam exam = examRepository.findById(attempt.getExamId())
                                  .orElseThrow(() -> new RuntimeException("Exam not found"));
        exam.setAttemptCount(exam.getAttemptCount() == null ? 1 : exam.getAttemptCount() + 1);
        examRepository.save(exam);

        return SubmitExamResponse.builder()
                                 .score(score)
                                 .totalQuestions(attempt.getTotalQuestions())
                                 .correctCount(correctCount)
                                 .finishedAt(attempt.getFinishedAt())
                                 .status(attempt.getStatus().name())
                                 .build();
    }

    @Override
    @Transactional
    public void delete(String attemptId) {
        ExamAttempt attempt = attemptRepository.findById(attemptId)
                                               .orElseThrow(() -> new RuntimeException("Attempt not found with id: " + attemptId));

        if (attempt.getStatus() == AttemptStatusType.IN_PROGRESS) {
            throw new RuntimeException("Không thể xóa bài thi đang trong quá trình làm");
        }

        attemptRepository.delete(attempt);
    }

    @Override
    public ResponsePage<List<AttemptHistoryDto>> getUserHistory(String examTitle,
                                                                 LocalDate startDateFrom,
                                                                 LocalDate startDateTo,
                                                                 Pageable pageable) {
        // Nếu có tìm kiếm theo tên bài thi → resolve examIds trước
        List<String> examIds = null;
        String normalizedexamTitle = Normalizer.normalize(examTitle, Normalizer.Form.NFC);
        if (normalizedexamTitle != null && !normalizedexamTitle.trim().isEmpty()) {
            List<Exam> exams = examRepository.findByStatusAndTitleContainingIgnoreCase(
                    StatusExamType.ACTIVE, normalizedexamTitle.trim());
            examIds = exams.stream().map(Exam::getId).toList();
            if (examIds.isEmpty()) {
                return ResponsePage.<List<AttemptHistoryDto>>builder()
                        .data(Collections.emptyList())
                        .totalElement(0)
                        .totalPage(0)
                        .pageSize(pageable.getPageSize())
                        .pageIndex(pageable.getPageNumber())
                        .build();
            }
        }

        LocalDateTime from = startDateFrom != null ? startDateFrom.atStartOfDay() : null;
        LocalDateTime to   = startDateTo   != null ? startDateTo.atTime(23, 59, 59) : null;

        String userId = SessionUtil.getCurrentUser().getId();

        // Sử dụng cờ boolean filterByExam và mảng rỗng để tránh lỗi cú pháp $in của MongoDB khi examIds là null
        boolean filterByExam = (examIds != null);
        List<String> safeExamIds = filterByExam ? examIds : Collections.emptyList();

        Page<ExamAttempt> page = attemptRepository.findAllWithFilters(userId, filterByExam, safeExamIds, from, to, pageable);

        // Lấy examTitle từ Exam thông qua examId
        List<String> ids = page.getContent().stream().map(ExamAttempt::getExamId).distinct().toList();
        Map<String, String> titleMap = examRepository.findAllById(ids)
                .stream().collect(Collectors.toMap(Exam::getId, Exam::getTitle));

        List<AttemptHistoryDto> dtos = page.getContent().stream().map(a -> {
            Long duration = null;
            if (a.getStartedAt() != null && a.getFinishedAt() != null) {
                duration = Duration.between(a.getStartedAt(), a.getFinishedAt()).getSeconds();
            }
            return AttemptHistoryDto.builder()
                    .attemptId(a.getId())
                    .examId(a.getExamId())
                    .examTitle(titleMap.getOrDefault(a.getExamId(), "Unknown"))
                    .score(a.getScore())
                    .totalQuestions(a.getTotalQuestions())
                    .startedAt(a.getStartedAt())
                    .durationSeconds(duration)
                    .status(a.getStatus() != null ? a.getStatus().name() : null)
                    .build();
        }).toList();

        return ResponsePage.<List<AttemptHistoryDto>>builder()
                .data(dtos)
                .totalElement(page.getTotalElements())
                .totalPage(page.getTotalPages())
                .pageSize(pageable.getPageSize())
                .pageIndex(pageable.getPageNumber())
                .build();
    }

    @Override
    public AttemptDetailDto getDetail(String attemptId) {

        ExamAttempt attempt = attemptRepository.findById(attemptId)
                                               .orElseThrow(() -> new RuntimeException("Attempt not found"));
        String userId = SessionUtil.getCurrentUser().getId();

        if (!attempt.getUserId().equals(userId)) {
            throw new RuntimeException("Không có quyền xem bài thi này");
        }

        Exam exam = examRepository.findById(attempt.getExamId())
                                  .orElseThrow(() -> new RuntimeException("Exam not found"));

        // Map questionId → ExamQuestion để lấy đáp án đúng và nội dung
        List<ExamQuestion> questions = questionRepository.findByExamId(attempt.getExamId());
        Map<String, ExamQuestion> questionMap = questions.stream()
                .collect(Collectors.toMap(ExamQuestion::getId, q -> q));

        List<AttemptDetailDto.AnswerDetailDto> answerDtos = Collections.emptyList();
        if (attempt.getAnswers() != null) {
            answerDtos = attempt.getAnswers().stream().map(ans -> {
                ExamQuestion q = questionMap.get(ans.getExamQuestionId());
                return AttemptDetailDto.AnswerDetailDto.builder()
                        .examQuestionId(ans.getExamQuestionId())
                        .content(q != null ? q.getContent() : null)
                        .selectedAnswer(ans.getSelectedAnswer())
                        .selectedKeys(ans.getSelectedKeys())
                        .correctAnswer(q != null ? q.getCorrectAnswer() : null)
                        .correctAnswerKeys(q != null ? q.getCorrectAnswerKeys() : null)
                        .isCorrect(ans.isCorrect())
                        .pointsEarned(ans.getPointsEarned())
                        .options(q != null ? convertOptions(q.getOptions()) : Collections.emptyList())
                        .build();
            }).toList();
        }

        Long duration = null;
        if (attempt.getStartedAt() != null && attempt.getFinishedAt() != null) {
            duration = Duration.between(attempt.getStartedAt(), attempt.getFinishedAt()).getSeconds();
        }

        return AttemptDetailDto.builder()
                .attemptId(attempt.getId())
                .examId(attempt.getExamId())
                .examTitle(exam.getTitle())
                .score(attempt.getScore())
                .totalQuestions(attempt.getTotalQuestions())
                .startedAt(attempt.getStartedAt())
                .finishedAt(attempt.getFinishedAt())
                .durationSeconds(duration)
                .status(attempt.getStatus() != null ? attempt.getStatus().name() : null)
                .answers(answerDtos)
                .build();
    }

    // ────────────────────────────────────────────
    private List<OptionResponse> convertOptions(List<OptionBank> options) {
        if (options == null) return Collections.emptyList();
        return options.stream()
                      .map(o -> OptionResponse.builder()
                                              .key(o.getKey())
                                              .text(o.getText())
                                              .build())
                      .toList();
    }
}