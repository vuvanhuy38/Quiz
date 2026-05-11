package com.webquiz.domain.service.impl;

import com.webquiz.domain.entity.Category;
import com.webquiz.domain.repository.CategoryRepository;
import com.webquiz.domain.repository.QuestionBankRepository;
import com.webquiz.domain.service.CategoryService;
import com.webquiz.web.dto.common.ResponsePage;
import com.webquiz.web.dto.request.category.CreateCategoryRequest;
import com.webquiz.web.dto.request.category.UpdateCategoryRequest;
import com.webquiz.web.dto.response.category.CategoryResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public void create(CreateCategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new RuntimeException("Tên danh mục đã tồn tại");
        }

        Category category = Category.builder()
                                    .name(request.getName())
                                    .description(request.getDescription())
                                    .build();

        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void update(String id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                                              .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));

        if (!category.getName().equals(request.getName()) &&
            categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new RuntimeException("Tên danh mục đã tồn tại");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        categoryRepository.save(category);
    }

    @Override
    public ResponsePage<List<CategoryResponse>> getAll(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);

        List<CategoryResponse> content = categories.map(category ->
                                                                CategoryResponse.builder()
                                                                                .id(category.getId())
                                                                                .name(category.getName())
                                                                                .description(category.getDescription())
                                                                                .build()
        ).getContent();

        return ResponsePage.<List<CategoryResponse>>builder()
                           .message("Lấy danh sách category thành công")
                           .data(content)
                           .totalElement(categories.getTotalElements())
                           .totalPage(categories.getTotalPages())
                           .pageSize(categories.getSize())
                           .pageIndex(categories.getNumber())
                           .build();
    }

    @Override
    @Transactional
    public void delete(String id) {
        Category category = categoryRepository.findById(id)
                                              .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));

        categoryRepository.delete(category);
    }
}
