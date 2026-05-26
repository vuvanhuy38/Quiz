package com.webquiz.web.dto.response.attempt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttemptHistoryDto {
    private String attemptId;
    private String examId;
    private String examTitle;
    private Double score;
    private Integer totalQuestions;
    private LocalDateTime startedAt;
    private Long durationSeconds;
    private String status;
}
