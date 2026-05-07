package com.webquiz.web.dto.request.attempt;

import lombok.Data;

import java.util.List;

@Data
public class SubmitExamRequest {
    private List<SubmitAnswerItemDto> answers;
}
