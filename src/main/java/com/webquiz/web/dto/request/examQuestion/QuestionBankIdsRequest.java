package com.webquiz.web.dto.request.examQuestion;

import lombok.Data;

import java.util.List;

@Data
public class QuestionBankIdsRequest {

    private List<String> questionBankIds;
}
