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

    @PostMapping("/create/{id}")
    public ResponseEntity<Response<Void>> createExam(@PathVariable String id,
                                                     @Valid @RequestBody List<ExamQuestionRequest> requests) {
        examQuestionService.upsertExamQuestion(id, requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                Response.<Void>builder()
                        .message("Tạo câu hỏi thành công")
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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response<Void>> deleteQuestion(@PathVariable String id) {
        examQuestionService.delete(id);
        return ResponseEntity.ok(
                Response.<Void>builder()
                        .message("Xóa câu hỏi thành công")
                        .build()
        );
    }
}
