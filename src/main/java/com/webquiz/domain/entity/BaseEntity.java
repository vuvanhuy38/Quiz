package com.webquiz.domain.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
public class BaseEntity {

    @Id
    private String id;

    @Field(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Field(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
