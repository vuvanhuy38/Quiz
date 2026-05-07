package com.webquiz.web.dto.request.examQuestion;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExamOptionDto {

    @NotBlank(message = "Khóa đáp án không được để trống")
    private String key;

    @NotBlank(message = "Nội dung đáp án không được để trống")
    private String text;
}
