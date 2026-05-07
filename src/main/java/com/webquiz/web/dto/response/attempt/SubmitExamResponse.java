package com.webquiz.web.dto.response.attempt;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SubmitExamResponse {

    private Double score;

    private Integer totalQuestions;

    private Long correctCount;

    private LocalDateTime finishedAt;

    private String status;
}
