package com.webquiz.web.dto.request.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCategoryRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    private String parentId;
}
