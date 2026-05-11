package com.webquiz.domain.service;


import com.webquiz.web.dto.common.ResponsePage;
import com.webquiz.web.dto.request.questionBank.CreateQuestionRequest;
import com.webquiz.web.dto.request.questionBank.UpdateQuestionRequest;
import com.webquiz.web.dto.response.questionBank.QuestionBankDetailResponse;
import com.webquiz.web.dto.response.questionBank.QuestionBankListResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionBankService {
    void create(CreateQuestionRequest request);
    void update(String id, UpdateQuestionRequest request);
    void delete(String id);
    ResponsePage<List<QuestionBankListResponse>> getList(Pageable pageable);
    QuestionBankDetailResponse getDetail(String id);
}
