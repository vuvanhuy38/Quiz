package com.webquiz.web.controller.resources;

import com.webquiz.domain.service.CategoryService;
import com.webquiz.web.dto.common.Response;
import com.webquiz.web.dto.common.ResponsePage;
import com.webquiz.web.dto.request.category.CreateCategoryRequest;
import com.webquiz.web.dto.request.category.UpdateCategoryRequest;
import com.webquiz.web.dto.response.category.CategoryResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@AllArgsConstructor
public class CategoryResources {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Response<Void>> create(@Valid @RequestBody CreateCategoryRequest request) {
        categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                Response.<Void>builder()
                        .message("Tạo category thành công")
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<Void>> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        categoryService.update(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<Void>builder()
                        .message("Cập nhập category thành công")
                        .build()
        );
    }

    @GetMapping("/getlist")
    public ResponseEntity<ResponsePage<List<CategoryResponse>>> getAll(
            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(
                categoryService.getAll(pageable)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Object>> delete(
            @PathVariable String id) {
        categoryService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                Response.builder()
                        .message("Xóa category thành công")
                        .build()
        );
    }
}
