package com.webquiz.domain.service;

import com.webquiz.web.dto.common.ResponsePage;
import com.webquiz.web.dto.request.attempt.SubmitExamRequest;
import com.webquiz.web.dto.response.attempt.AttemptDetailDto;
import com.webquiz.web.dto.response.attempt.AttemptHistoryDto;
import com.webquiz.web.dto.response.attempt.ExamQuestionResponse;
import com.webquiz.web.dto.response.attempt.StartAttemptResponse;
import com.webquiz.web.dto.response.attempt.SubmitExamResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ExamAttemptService {

    StartAttemptResponse startAttempt(String examId);
    List<ExamQuestionResponse> getQuestions(String attemptId);
    SubmitExamResponse submitExam(String attemptId, SubmitExamRequest request);
    void delete(String attemptId);

    ResponsePage<List<AttemptHistoryDto>> getUserHistory(String examTitle,
                                                         LocalDate startDateFrom,
                                                         LocalDate startDateTo,
                                                         Pageable pageable);

    AttemptDetailDto getDetail(String attemptId);
}
