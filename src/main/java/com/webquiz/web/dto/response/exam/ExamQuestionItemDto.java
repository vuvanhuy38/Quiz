package com.webquiz.web.dto.response.exam;

import com.webquiz.contact.enums.LevelType;
import com.webquiz.contact.enums.QuestionType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ExamQuestionItemDto {

    private String id;

    private String content;

    private QuestionType type;

    private List<ExamOptionBankDto> options;

    private String correctAnswer;

    private List<String> correctAnswerKeys;

    private LevelType level;
}
