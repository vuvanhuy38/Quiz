package com.webquiz.web.dto.request.attempt;

import lombok.Data;

import java.util.List;

@Data
public class SubmitAnswerItemDto {

    private String examQuestionId;

    private String selectedAnswer;

    private List<String> selectedKeys;
}
