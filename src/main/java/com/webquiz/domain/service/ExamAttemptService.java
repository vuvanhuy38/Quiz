package com.webquiz.domain.service;

import com.webquiz.web.dto.common.Response;
import com.webquiz.web.dto.request.attempt.SubmitExamRequest;
import com.webquiz.web.dto.response.attempt.ExamQuestionResponse;
import com.webquiz.web.dto.response.attempt.StartAttemptResponse;
import com.webquiz.web.dto.response.attempt.SubmitExamResponse;

import java.util.List;

public interface ExamAttemptService {

    StartAttemptResponse startAttempt(String examId);
    List<ExamQuestionResponse> getQuestions(String attemptId);
    SubmitExamResponse submitExam(String attemptId, SubmitExamRequest request);
}
