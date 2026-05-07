package com.webquiz.domain.service;

import com.webquiz.web.dto.request.questionBank.CreateQuestionRequest;
import com.webquiz.web.dto.request.questionBank.UpdateQuestionRequest;

public interface QuestionBankService {
    void create(CreateQuestionRequest request);
    void update(String id, UpdateQuestionRequest request);
}
