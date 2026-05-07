package com.webquiz.web.dto.request.examQuestion;

import com.webquiz.contact.enums.LevelType;
import com.webquiz.contact.enums.QuestionType;
import lombok.Data;

import java.util.List;

@Data
public class ExamQuestionRequest {
    private String id;            // null → create, có giá trị → update
    private String content;
    private String categoryId;
    private QuestionType type;
    private List<ExamOptionDto> options;
    private String correctAnswer;
    private List<String> correctAnswerKeys;
    private LevelType level;
}
