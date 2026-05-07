package com.webquiz.domain.service;

import com.webquiz.domain.entity.Exam;
import com.webquiz.web.dto.request.exam.CreateExamRequest;
import com.webquiz.web.dto.request.exam.UpdateExamRequest;
import com.webquiz.web.dto.response.exam.ExamItemResponse;
import com.webquiz.web.dto.response.exam.ExamResponse;

import java.util.List;

public interface ExamService {
    void create(CreateExamRequest createExamRequeste);
    void update(String id, UpdateExamRequest updateExamRequeste);
    ExamResponse getHome();
    List<ExamItemResponse> search(String title);
}
