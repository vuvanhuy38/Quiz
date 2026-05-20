package com.webquiz.web.controller.resources;

import com.webquiz.contact.enums.StatusExamType;
import com.webquiz.domain.service.ExamService;
import com.webquiz.web.dto.common.Response;
import com.webquiz.web.dto.common.ResponsePage;
import com.webquiz.web.dto.request.exam.CreateExamRequest;
import com.webquiz.web.dto.request.exam.UpdateExamRequest;
import com.webquiz.web.dto.response.exam.ExamDetailResponse;
import com.webquiz.web.dto.response.exam.ExamItemResponse;
import com.webquiz.web.dto.response.exam.ExamResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@AllArgsConstructor
public class ExamResources {

    private final ExamService examService;

    @PostMapping("/create")
    public ResponseEntity<Response<String>> createExam(@Valid @RequestBody CreateExamRequest request) {
        String id = examService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                Response.<String>builder()
                        .data(id)
                        .message("Tạo đề thi thành công")
                        .build()
        );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Response<Void>> updateExam(@PathVariable String id,
                                                     @Valid @RequestBody UpdateExamRequest request) {
        examService.update(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<Void>builder()
                        .message("Cập nhật thành công")
                        .build()
        );
    }

    @GetMapping("/home")
    public ResponseEntity<Response<ExamResponse>> getHome() {
        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<ExamResponse>builder()
                        .data(examService.getHome())
                        .build()
        );
    }

    @GetMapping("/search")
    public ResponseEntity<Response<List<ExamItemResponse>>> search(
            @RequestParam String title) {
        return ResponseEntity.ok(
                Response.<List<ExamItemResponse>>builder()
                        .data(examService.search(title))
                        .build()
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response<Void>> deleteExam(@PathVariable String id) {
        examService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<Void>builder()
                        .message("Xóa đề thi thành công")
                        .build()
        );
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Response<ExamDetailResponse>> getExamDetail(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<ExamDetailResponse>builder()
                        .message("Lấy chi tiết đề thi thành công")
                        .data(examService.getExamDetail(id))
                        .build()
        );
    }

    @GetMapping("/getAll")
    public ResponseEntity<ResponsePage<List<ExamItemResponse>>> getAllExams(
            @RequestParam(required = false,defaultValue = "") String title,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) StatusExamType status,
            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(
                examService.getAllExams(pageable, title, categoryId, status)
        );
    }
}
