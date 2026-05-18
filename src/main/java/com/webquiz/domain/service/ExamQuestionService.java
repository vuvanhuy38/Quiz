package com.webquiz.domain.service;

import com.webquiz.web.dto.request.examQuestion.ExamQuestionRequest;

import java.util.List;

public interface ExamQuestionService {

    void upsertExamQuestion(String examId, List<ExamQuestionRequest> requests);
    List<ExamQuestionRequest> previewFromBank(List<String> questionBankIds);
    void delete(String examQuestionId);
}
