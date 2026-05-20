package com.webquiz.web.dto.response.exam;


import com.webquiz.contact.enums.StatusExamType;
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

    private String parentCategoryId;

    private Integer timeLimit;

    private Integer totalQuestions;

    private Long attemptCount;

    private StatusExamType status;

    private String createdBy;

    private List<ExamQuestionItemDto> questions;
}
