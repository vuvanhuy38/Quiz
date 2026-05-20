package com.webquiz.web.dto.response.questionBank;

import com.webquiz.contact.enums.LevelType;
import com.webquiz.contact.enums.QuestionType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionBankModalResponse {
    private String id;
    private String content;
    private String categoryName;
    private QuestionType type;
    private LevelType level;

    private List<OptionBankResponse> options;
    private String correctAnswer;
    private List<String> correctAnswerKeys;
}
