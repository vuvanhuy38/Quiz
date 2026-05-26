package com.webquiz.web.dto.response.attempt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttemptDetailDto {
    private String attemptId;
    private String examId;
    private String examTitle;
    private Double score;
    private Integer totalQuestions;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Long durationSeconds;
    private String status;
    private List<AnswerDetailDto> answers;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AnswerDetailDto {
        private String examQuestionId;
        private String content;
        private String selectedAnswer;
        private List<String> selectedKeys;
        private String correctAnswer;
        private List<String> correctAnswerKeys;
        private boolean isCorrect;
        private Integer pointsEarned;
        private List<OptionResponse> options;
    }
}
