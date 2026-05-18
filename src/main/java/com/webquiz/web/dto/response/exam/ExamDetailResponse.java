package com.webquiz.web.dto.response.exam;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ExamDetailResponse {

    private String id;

    private String title;

    private String description;

    private String categoryId;

    private Integer timeLimit;

    private Integer totalQuestions;

    private Long attemptCount;

    private String createdBy;

    private List<ExamQuestionItemDto> questions;
}
