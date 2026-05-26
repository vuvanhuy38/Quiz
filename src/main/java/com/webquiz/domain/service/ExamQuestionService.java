package com.webquiz.domain.service;

import com.webquiz.web.dto.request.examQuestion.ExamQuestionRequest;
import com.webquiz.web.dto.request.examQuestion.ImportQuestionBankRequest;

import java.util.List;

public interface ExamQuestionService {

    void upsertExamQuestion(String examId, List<ExamQuestionRequest> requests);
    List<ExamQuestionRequest> previewFromBank(ImportQuestionBankRequest request);
    void delete(String examQuestionId);
}
