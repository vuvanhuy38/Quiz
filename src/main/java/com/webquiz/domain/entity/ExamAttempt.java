package com.webquiz.domain.entity;

import com.webquiz.contact.enums.AttemptStatusType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Document(collection = "exam_attempts")
public class ExamAttempt extends BaseEntity {

    @Field(name = "user_id")
    private String userId;

    @Field(name = "exam_id")
    private String examId;

    @Field(name = "answers")
    private List<Answers> answers;

    @Field(name = "score")
    private Double score;

    @Field(name = "total_questions")
    private Integer totalQuestions;

    @Field(name = "started_at")
    private LocalDateTime startedAt;

    @Field(name = "finished_at")
    private LocalDateTime finishedAt;

    @Field(name = "status")
    private AttemptStatusType status;
}
