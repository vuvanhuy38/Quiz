package com.webquiz.web.controller.resources;

import com.webquiz.domain.service.ExamQuestionService;
import com.webquiz.web.dto.common.Response;
import com.webquiz.web.dto.request.examQuestion.ExamQuestionRequest;
import com.webquiz.web.dto.request.examQuestion.QuestionBankIdsRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam-question")
@AllArgsConstructor
public class ExamQuestionResources {

    private final ExamQuestionService examQuestionService;

    @PostMapping("/create/{examId}")
    public ResponseEntity<Response<Void>> createExam(@PathVariable String examId,
                                                     @Valid @RequestBody List<ExamQuestionRequest> requests) {
        examQuestionService.upsertExamQuestion(examId, requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                Response.<Void>builder()
                        .message("Tạo đề thi thành công")
                        .build()
        );
    }

    @PostMapping("/preview")
    public ResponseEntity<Response<List<ExamQuestionRequest>>> preview(
            @RequestBody QuestionBankIdsRequest request) {

        List<ExamQuestionRequest> data =
                examQuestionService.previewFromBank(request.getQuestionBankIds());

        return ResponseEntity.ok(
                Response.<List<ExamQuestionRequest>>builder()
                        .data(data)
                        .message("Preview thành công")
                        .build()
        );
    }
}
