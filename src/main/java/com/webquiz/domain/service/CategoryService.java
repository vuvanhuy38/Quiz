package com.webquiz.domain.service;

import com.webquiz.domain.entity.Category;
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
    ResponsePage<List<CategoryResponse>> getAll(String name , Pageable pageable);
    void delete(String id);
    List<CategoryResponse> getParentCategories();
    List<CategoryResponse> getChildCategories(String parentId);
    List<CategoryResponse> getAllChildCategories();
    CategoryResponse getById(String id);
}
