package com.webquiz.web.dto.response.questionBank;

import com.webquiz.contact.enums.LevelType;
import com.webquiz.contact.enums.QuestionType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionBankDetailResponse {
    private String id;
    private String content;
    private String categoryId;
    private String parentCategoryId;
    private String categoryName;
    private QuestionType type;
    private List<OptionBankResponse> options;
    private String correctAnswer;
    private List<String> correctAnswerKeys;
    private LevelType level;
}
