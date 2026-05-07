package com.webquiz.domain.entity;

import com.webquiz.contact.enums.StatusExamType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@Document(collection = "exams")
public class Exam extends BaseEntity {

    @Field(name = "title")
    private String title;

    @Field(name = "description")
    private String description;

    @Field(name = "time_limit")
    private Integer timeLimit;

    @Field(name = "total_questions")
    private Integer totalQuestions;

    @Field(name = "attempt_count")
    private Long attemptCount;

    @Field(name = "status")
    private StatusExamType status;

    @Field(name = "created_by")
    private String createdBy;
}
