package com.webquiz.web.dto.response.exam;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ExamItemResponse {

    private String id;

    private String title;

    private String description;

    private Integer totalQuestions;

    private Integer timeLimit;

    private Long attemptCount;

    private LocalDateTime createdAt;
}
