package com.webquiz.domain.service;

import com.webquiz.web.dto.common.ResponsePage;
import com.webquiz.web.dto.request.category.CreateCategoryRequest;
import com.webquiz.web.dto.request.category.UpdateCategoryRequest;
import com.webquiz.web.dto.response.category.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    void create(CreateCategoryRequest request);
    void update(String id, UpdateCategoryRequest request);
    ResponsePage<List<CategoryResponse>> getAll(Pageable pageable);
    void delete(String id);
}
