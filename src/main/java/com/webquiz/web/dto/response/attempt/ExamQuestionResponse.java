package com.webquiz.web.dto.response.attempt;

import com.webquiz.domain.entity.OptionBank;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class ExamQuestionResponse {

    private String questionId;

    private String content;

    private String type;

    private List<OptionResponse> options;
}
