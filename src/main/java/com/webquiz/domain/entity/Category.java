package com.webquiz.domain.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@Document(collection = "categorys")
public class Category extends BaseEntity{

    @Field(name = "name")
    private String name;

    @Field(name = "description")
    private String description;

    @Field(name = "parent_id")
    private String parentId;
}
