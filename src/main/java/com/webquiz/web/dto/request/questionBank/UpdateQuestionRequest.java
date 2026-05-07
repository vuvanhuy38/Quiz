package com.webquiz.web.dto.request.questionBank;

import com.webquiz.contact.enums.LevelType;
import com.webquiz.contact.enums.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UpdateQuestionRequest {

    @NotBlank(message = "Nội dung câu hỏi không được để trống")
    private String content;

    @NotBlank(message = "Danh mục câu hỏi không được để trống")
    private String categoryId;

    @NotNull(message = "Loại câu hỏi không được để trống")
    private QuestionType type;

    private List<OptionBankDto> options;

    private String correctAnswer;

    private List<String> correctAnswerKeys;

    @NotNull(message = "Mức độ câu hỏi không được để trống")
    private LevelType level;
}
