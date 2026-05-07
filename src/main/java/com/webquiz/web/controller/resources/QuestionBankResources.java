package com.webquiz.web.controller.resources;

import com.webquiz.domain.service.QuestionBankService;
import com.webquiz.web.dto.common.Response;
import com.webquiz.web.dto.request.questionBank.CreateQuestionRequest;
import com.webquiz.web.dto.request.questionBank.UpdateQuestionRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/question-bank")
@AllArgsConstructor
public class QuestionBankResources {

    private final QuestionBankService questionBankService;


    @PostMapping("create")
    public ResponseEntity<Response<Void>> createQuestion(@Valid @RequestBody CreateQuestionRequest request) {
        questionBankService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                Response.<Void>builder()
                        .message("Tạo câu hỏi thành công")
                        .build()
        );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Response<Void>> updateQuestion(@PathVariable String id,
                                                         @Valid @RequestBody UpdateQuestionRequest request) {
        questionBankService.update(id, request);
        return ResponseEntity.ok(
                Response.<Void>builder()
                        .message("Cập nhập câu ho thành công")
                        .build()
        );
    }
}
