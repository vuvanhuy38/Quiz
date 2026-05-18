package com.webquiz.web.dto.response.exam;

import com.webquiz.contact.enums.StatusExamType;
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

    private StatusExamType status;

    private LocalDateTime createdAt;
}
