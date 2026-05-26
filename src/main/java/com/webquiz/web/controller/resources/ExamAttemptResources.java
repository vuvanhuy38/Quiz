package com.webquiz.web.controller.resources;

import com.webquiz.contact.SessionUtil;
import com.webquiz.domain.service.ExamAttemptService;
import com.webquiz.web.dto.common.Response;
import com.webquiz.web.dto.common.ResponsePage;
import com.webquiz.web.dto.request.attempt.SubmitExamRequest;
import com.webquiz.web.dto.response.attempt.AttemptDetailDto;
import com.webquiz.web.dto.response.attempt.AttemptHistoryDto;
import com.webquiz.web.dto.response.attempt.ExamQuestionResponse;
import com.webquiz.web.dto.response.attempt.StartAttemptResponse;
import com.webquiz.web.dto.response.attempt.SubmitExamResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public ResponseEntity<Response<List<ExamQuestionResponse>>> getQuestions(@PathVariable String id) {
        List<ExamQuestionResponse> response = examAttemptService.getQuestions(id);
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

    @GetMapping("/history")
    public ResponseEntity<ResponsePage<List<AttemptHistoryDto>>> getHistory(
            @RequestParam(required = false, defaultValue = "") String examTitle,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDateTo,
            Pageable pageable) {


        return ResponseEntity.ok(
                examAttemptService.getUserHistory(examTitle, startDateFrom, startDateTo, pageable)
        );
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Response<AttemptDetailDto>> getDetail(@PathVariable String id) {
        return ResponseEntity.ok(
                Response.<AttemptDetailDto>builder()
                        .data(examAttemptService.getDetail(id))
                        .build()
        );
    }
}
