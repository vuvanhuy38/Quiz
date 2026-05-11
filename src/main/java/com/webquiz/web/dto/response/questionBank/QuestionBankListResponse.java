package com.webquiz.web.dto.response.questionBank;

import com.webquiz.contact.enums.LevelType;
import com.webquiz.contact.enums.QuestionType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionBankListResponse {
    private String id;
    private String content;
    private String categoryId;
    private QuestionType type;
    private LevelType level;
}