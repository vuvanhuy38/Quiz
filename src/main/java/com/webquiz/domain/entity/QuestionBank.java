package com.webquiz.domain.entity;

import com.webquiz.contact.enums.LevelType;
import com.webquiz.contact.enums.QuestionType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Builder
@Document(collection = "question_banks")
public class QuestionBank extends BaseEntity {

    @Field(name = "content")
    private String content;

    @Field(name = "category_id")
    private String categoryId;

    @Field(name = "type")
    private QuestionType type;

    @Field(name = "options")
    private List<OptionBank> options;

    @Field(name = "correct_answer")
    private String correctAnswer;

    @Field(name = "correct_answer_keys")
    private List<String> correctAnswerKeys;

    @Field(name = "level")
    private LevelType level;
}
