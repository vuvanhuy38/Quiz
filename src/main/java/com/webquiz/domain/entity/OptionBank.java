package com.webquiz.domain.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
public class OptionBank {

    @Field(name = "key")
    private String key;

    @Field(name = "text")
    private String text;
}
