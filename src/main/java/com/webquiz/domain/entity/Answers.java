package com.webquiz.domain.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Builder
public class Answers {

    @Field(name = "exam_question_id")
    private String examQuestionId;

    @Field(name = "selected_answer")
    private String selectedAnswer;

    @Field(name = "selected_keys")
    private List<String> selectedKeys;

    @Field(name = "is_correct")
    private boolean isCorrect;

    @Field(name = "points_earned")
    private Integer pointsEarned;
}
