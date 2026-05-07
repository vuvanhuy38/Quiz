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
    private final QuestionBankRepository questionBankRepository;

    @Override
    @Transactional
    public void create(CreateCategoryRequest request) {

        // validate parent
        if (request.getParentId() != null) {

            Category parent = categoryRepository.findById(request.getParentId())
                                                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục cha"));

            // không cho category con làm cha tiếp
            if (parent.getParentId() != null) {
                throw new RuntimeException("Danh mục cha phải là danh mục gốc");
            }
        }

        Category category = Category.builder().name(request.getName()).description(request.getDescription())
                                    .parentId(request.getParentId()).build();

        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void update(String id, UpdateCategoryRequest request) {

        Category category = categoryRepository.findById(id)
                                              .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));

        if (!category.getName().equals(request.getName()) &&
            categoryRepository.existsByNameAndParentId(request.getName(), category.getParentId())) {
            throw new RuntimeException("Tên danh mục đã tồn tại ở cấp này");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        categoryRepository.save(category);
    }

    @Override
    public ResponsePage<List<CategoryResponse>> getAll(Pageable pageable) {
        // chỉ phân trang category cha
        Page<Category> parentCategories =
                categoryRepository.findByParentIdIsNull(pageable);

        List<CategoryResponse> content =
                parentCategories.map(parent -> {

                    List<CategoryResponse> children =
                            categoryRepository.findByParentId(parent.getId())
                                              .stream()
                                              .map(child -> CategoryResponse.builder()
                                                                            .id(child.getId())
                                                                            .name(child.getName())
                                                                            .description(child.getDescription())
                                                                            .parentId(child.getParentId())
                                                                            .children(List.of())
                                                                            .build())
                                              .toList();

                    return CategoryResponse.builder()
                                           .id(parent.getId())
                                           .name(parent.getName())
                                           .description(parent.getDescription())
                                           .parentId(parent.getParentId())
                                           .children(children)
                                           .build();

                }).getContent();

        return ResponsePage.<List<CategoryResponse>>builder()
                           .message("Lấy danh sách category thành công")
                           .data(content)
                           .totalElement(parentCategories.getTotalElements())
                           .totalPage(parentCategories.getTotalPages())
                           .pageSize(parentCategories.getSize())
                           .pageIndex(parentCategories.getNumber())
                           .build();
    }

    @Override
    @Transactional
    public void delete(String id) {
        Category category = categoryRepository.findById(id)
                                              .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
        // category cha
        if (category.getParentId() == null) {
            // lấy category con
            List<Category> children = categoryRepository.findByParentId(id);

            // xóa question của category con
            for (Category child : children) {
                questionBankRepository.deleteByCategoryId(child.getId());
            }
            // xóa question của category cha
            questionBankRepository.deleteByParentCategoryId(id);
            // xóa category con
            categoryRepository.deleteAll(children);
        }
        // category con
        else {
            // xóa question của category con
            questionBankRepository.deleteByCategoryId(id);
        }
        // xóa category hiện tại
        categoryRepository.delete(category);
    }
}
