package com.webquiz.web.dto.response.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PopularExamDto {

    private String id;
    private String title;
    private int totalQuestions;
    private long attemptCount;
    private String status;
}
