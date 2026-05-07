package com.webquiz.web.dto.response.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CategoryResponse {

    private String id;

    private String name;

    private String description;

    private String parentId;

    private List<CategoryResponse> children;
}