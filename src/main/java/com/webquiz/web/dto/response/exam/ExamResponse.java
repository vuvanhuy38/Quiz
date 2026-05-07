package com.webquiz.web.dto.response.exam;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ExamResponse {

    private List<ExamItemResponse> featuredExams;

    private List<ExamItemResponse> latestExams;
}
