package com.webquiz.web.controller.resources;

import com.webquiz.domain.service.QuestionBankService;
import com.webquiz.web.dto.common.Response;
import com.webquiz.web.dto.common.ResponsePage;
import com.webquiz.web.dto.request.questionBank.CreateQuestionRequest;
import com.webquiz.web.dto.request.questionBank.UpdateQuestionRequest;
import com.webquiz.web.dto.response.questionBank.QuestionBankDetailResponse;
import com.webquiz.web.dto.response.questionBank.QuestionBankListResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response<Void>> deleteQuestion(@PathVariable String id) {
        questionBankService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<Void>builder()
                        .message("Xóa câu hỏi thành công")
                        .build()
        );
    }

    @GetMapping("/list")
    public ResponseEntity<ResponsePage<List<QuestionBankListResponse>>> getList( @RequestParam(required = false) String type,
                                                                                 @RequestParam(required = false) String level,
                                                                                 @RequestParam(required = false,defaultValue = "") String content,
                                                                                 Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(questionBankService.getList(pageable, type, level, content));
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Response<QuestionBankDetailResponse>> getDetail(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<QuestionBankDetailResponse>builder()
                        .data(questionBankService.getDetail(id))
                        .build()
        );
    }
}
