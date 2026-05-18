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

    @PostMapping("/start/{id}")
    public ResponseEntity<Response<StartAttemptResponse>> start(@PathVariable String id) {
        StartAttemptResponse response = examAttemptService.startAttempt(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                Response.<StartAttemptResponse>builder()
                        .data(response)
                        .build());
    }

    @GetMapping("/{id}/questions")
    public ResponseEntity<Response<List<ExamQuestionResponse>>> getQuestions(
            @PathVariable String id) {

        List<ExamQuestionResponse> response =
                examAttemptService.getQuestions(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<List<ExamQuestionResponse>>builder()
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<Response<SubmitExamResponse>> submitExam(
            @PathVariable String id,
            @RequestBody SubmitExamRequest request) {

        SubmitExamResponse response = examAttemptService.submitExam(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<SubmitExamResponse>builder()
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response<Void>> deleteAttempt(@PathVariable String id) {
        examAttemptService.delete(id);
        return ResponseEntity.ok(
                Response.<Void>builder()
                        .message("Xóa lịch sử làm bài thành công")
                        .build()
        );
    }
}
