package com.webquiz.domain.service.impl;

import com.webquiz.contact.SessionUtil;
import com.webquiz.contact.enums.AttemptStatusType;
import com.webquiz.contact.enums.QuestionType;
import com.webquiz.domain.entity.*;
import com.webquiz.domain.repository.ExamAttemptRepository;
import com.webquiz.domain.repository.ExamQuestionRepository;
import com.webquiz.domain.repository.ExamRepository;
import com.webquiz.domain.service.ExamAttemptService;
import com.webquiz.security.CustomUserDetails;
import com.webquiz.web.dto.request.attempt.SubmitAnswerItemDto;
import com.webquiz.web.dto.request.attempt.SubmitExamRequest;
import com.webquiz.web.dto.response.attempt.ExamQuestionResponse;
import com.webquiz.web.dto.response.attempt.OptionResponse;
import com.webquiz.web.dto.response.attempt.StartAttemptResponse;
import com.webquiz.web.dto.response.attempt.SubmitExamResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
        String username = user.getId();

        ExamAttempt attempt = ExamAttempt.builder()
                                         .examId(exam.getId())
                                         .userId(username)
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

        List<ExamQuestion> questions =
                questionRepository.findByExamId(attempt.getExamId());

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
                        && question.getCorrectAnswer()
                                   .equalsIgnoreCase(item.getSelectedAnswer());

                case MULTIPLE_CHOICE ->
                        item.getSelectedKeys() != null
                        && question.getCorrectAnswerKeys() != null
                        && new HashSet<>(item.getSelectedKeys())
                                .equals(new HashSet<>(question.getCorrectAnswerKeys()));
            };

            int points = isCorrect ? 1 : 0;

            if (isCorrect) {
                correctCount++;
            }

            Answers answer = Answers.builder()
                                    .examQuestionId(item.getExamQuestionId())
                                    .selectedAnswer(item.getSelectedAnswer())
                                    .selectedKeys(item.getSelectedKeys())
                                    .isCorrect(isCorrect)
                                    .pointsEarned(points)
                                    .build();

            System.out.println("REQUEST = " + item);
            System.out.println("FOUND QUESTION = " + question);
            System.out.println("CORRECT = " + question.getCorrectAnswer());
            System.out.println("USER = " + item.getSelectedAnswer());

            answerList.add(answer);
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

        Long currentAttemptCount = exam.getAttemptCount() == null ? 0 : exam.getAttemptCount();
        exam.setAttemptCount(currentAttemptCount + 1);
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


    private List<OptionResponse> convertOptions(List<OptionBank> options) {

        return options.stream()
                      .map(o -> OptionResponse.builder()
                                              .key(o.getKey())
                                              .text(o.getText())
                                              .build())
                      .toList();
    }
}