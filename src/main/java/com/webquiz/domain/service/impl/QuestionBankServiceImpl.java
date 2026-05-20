package com.webquiz.domain.service.impl;

import com.webquiz.contact.validate.QuestionValidator;
import com.webquiz.domain.entity.Category;
import com.webquiz.domain.entity.OptionBank;
import com.webquiz.domain.entity.QuestionBank;
import com.webquiz.domain.repository.CategoryRepository;
import com.webquiz.domain.repository.QuestionBankRepository;
import com.webquiz.domain.service.QuestionBankService;
import com.webquiz.web.dto.common.ResponsePage;
import com.webquiz.web.dto.request.questionBank.CreateQuestionRequest;
import com.webquiz.web.dto.request.questionBank.OptionBankDto;
import com.webquiz.web.dto.request.questionBank.UpdateQuestionRequest;
import com.webquiz.web.dto.response.questionBank.OptionBankResponse;
import com.webquiz.web.dto.response.questionBank.QuestionBankDetailResponse;
import com.webquiz.web.dto.response.questionBank.QuestionBankListResponse;
import com.webquiz.web.dto.response.questionBank.QuestionBankModalResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;

@Service
@AllArgsConstructor
public class QuestionBankServiceImpl implements QuestionBankService {

    private final QuestionBankRepository questionBankRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public void create(CreateQuestionRequest request) {
        QuestionValidator.validate(request.getType(), request.getOptions(), request.getCorrectAnswer(),
                                   request.getCorrectAnswerKeys());

        QuestionBank question = QuestionBank.builder()
                                            .content(request.getContent())
                                            .categoryId(request.getCategoryId())
                                            .type(request.getType())
                                            .options(mapOptions(request.getOptions()))
                                            .correctAnswer(request.getCorrectAnswer())
                                            .correctAnswerKeys(request.getCorrectAnswerKeys())
                                            .level(request.getLevel())
                                            .build();

        questionBankRepository.save(question);
    }

    @Override
    @Transactional
    public void update(String id, UpdateQuestionRequest request) {
        QuestionValidator.validate(request.getType(), request.getOptions(), request.getCorrectAnswer(),
                                   request.getCorrectAnswerKeys());

        QuestionBank question = questionBankRepository.findById(id)
                                                  .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi với id: " + id));

        question.setContent(request.getContent());
        question.setCategoryId(request.getCategoryId());
        question.setType(request.getType());
        question.setOptions(mapOptions(request.getOptions()));
        question.setCorrectAnswer(request.getCorrectAnswer());
        question.setCorrectAnswerKeys(request.getCorrectAnswerKeys());
        question.setLevel(request.getLevel());

        questionBankRepository.save(question);
    }

    @Override
    public void delete(String id) {
        QuestionBank question = questionBankRepository.findById(id)
                                                      .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi với id: " + id));

        questionBankRepository.delete(question);
    }

    @Override
    public ResponsePage<List<QuestionBankListResponse>> getList(Pageable pageable, String type, String level, String content, String categoryId) {
        String normalizedcontent = Normalizer.normalize(content, Normalizer.Form.NFC);
        Page<QuestionBank> page = questionBankRepository.findAllWithFilters( type, level, normalizedcontent, categoryId, pageable);


        List<QuestionBankListResponse> contents =
                page.getContent().stream().map(q -> {
                    String categoryName = categoryRepository.findById(q.getCategoryId())
                            .map(Category::getName)
                            .orElse("Không có danh mục");

                    return QuestionBankListResponse.builder()
                            .id(q.getId())
                            .content(q.getContent())
                            .categoryName(categoryName)
                            .type(q.getType())
                            .level(q.getLevel())
                            .build();
                }).toList();

        return ResponsePage.<List<QuestionBankListResponse>>builder()
                           .data(contents)
                           .totalElement(page.getTotalElements())
                           .totalPage(page.getTotalPages())
                           .pageSize(page.getSize())
                           .pageIndex(page.getNumber())
                           .build();
    }

    @Override
    public QuestionBankDetailResponse getDetail(String id) {
        QuestionBank question = questionBankRepository.findById(id)
                                                      .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi với id: " + id));

        Category category = null;
        if (question.getCategoryId() != null) {
            category = categoryRepository.findById(question.getCategoryId()).orElse(null);
        }

        return QuestionBankDetailResponse.builder()
                                         .id(question.getId())
                                         .content(question.getContent())
                                         .categoryId(question.getCategoryId())
                                         .parentCategoryId(category != null ? category.getParentId() : null)
                                         .categoryName(category.getName())
                                         .type(question.getType())
                                         .options(mapToOptionDtos(question.getOptions()))
                                         .correctAnswer(question.getCorrectAnswer())
                                         .correctAnswerKeys(question.getCorrectAnswerKeys())
                                         .level(question.getLevel())
                                         .build();
    }

    @Override
    public ResponsePage<List<QuestionBankModalResponse>> getModelList(Pageable pageable, String type, String level, String content, String categoryId) {

        String normalizedContent = (content == null) ? "" : Normalizer.normalize(content, Normalizer.Form.NFC);

        Page<QuestionBank> page = questionBankRepository.findAllWithFilters(type, level, normalizedContent, categoryId, pageable);

        List<QuestionBankModalResponse> contents = page.getContent().stream()
                                                       .map(q -> {
                                                           String categoryName = categoryRepository.findById(q.getCategoryId())
                                                                                                   .map(Category::getName)
                                                                                                   .orElse("Không có danh mục");
                                                           return QuestionBankModalResponse.builder()
                                                                                           .id(q.getId())
                                                                                           .content(q.getContent())
                                                                                           .categoryName(categoryName)
                                                                                           .type(q.getType())
                                                                                           .level(q.getLevel())
                                                                                           .options(mapToOptionDtos(q.getOptions()))
                                                                                           .correctAnswer(q.getCorrectAnswer())
                                                                                           .correctAnswerKeys(q.getCorrectAnswerKeys())
                                                                                           .build();
                                                       })
                                                       .toList();

        return ResponsePage.<List<QuestionBankModalResponse>>builder()
                           .data(contents)
                           .totalElement(page.getTotalElements())
                           .totalPage(page.getTotalPages())
                           .pageSize(page.getSize())
                           .pageIndex(page.getNumber())
                           .build();
    }

    private List<OptionBank> mapOptions(List<OptionBankDto> options) {
        if (options == null) return null;

        return options.stream()
                      .map(o -> OptionBank.builder()
                                          .key(o.getKey())
                                          .text(o.getText())
                                          .build())
                      .toList();
    }


    private List<OptionBankResponse> mapToOptionDtos(List<OptionBank> options) {
        if (options == null) return null;

        return options.stream()
                      .map(o -> OptionBankResponse.builder()
                                             .key(o.getKey())
                                             .text(o.getText())
                                             .build())
                      .toList();
    }
}
