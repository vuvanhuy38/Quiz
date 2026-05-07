package com.webquiz.web.controller.resources;

import com.webquiz.domain.service.ExamAttemptService;
import com.webquiz.web.dto.common.Response;
import com.webquiz.web.dto.request.attempt.SubmitExamRequest;
import com.webquiz.web.dto.response.attempt.ExamQuestionResponse;
import com.webquiz.web.dto.response.attempt.StartAttemptResponse;
import com.webquiz.web.dto.response.attempt.SubmitExamResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attempts")
@AllArgsConstructor
public class ExamAttemptResources {

    private final ExamAttemptService examAttemptService;

    @PostMapping("/start/{examId}")
    public ResponseEntity<Response<StartAttemptResponse>> start(@PathVariable String examId) {
        StartAttemptResponse response = examAttemptService.startAttempt(examId);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                Response.<StartAttemptResponse>builder()
                        .data(response)
                        .build());
    }

    @GetMapping("/{attemptId}/questions")
    public ResponseEntity<Response<List<ExamQuestionResponse>>> getQuestions(
            @PathVariable String attemptId) {

        List<ExamQuestionResponse> response =
                examAttemptService.getQuestions(attemptId);

        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<List<ExamQuestionResponse>>builder()
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/{attemptId}/submit")
    public ResponseEntity<Response<SubmitExamResponse>> submitExam(
            @PathVariable String attemptId,
            @RequestBody SubmitExamRequest request) {

        SubmitExamResponse response = examAttemptService.submitExam(attemptId, request);

        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<SubmitExamResponse>builder()
                        .data(response)
                        .build()
        );
    }

}
