package com.webquiz.web.dto.request.questionBank;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OptionBankDto {

    @NotBlank(message = "Khóa đáp án không được để trống")
    private String key;

    @NotBlank(message = "Nội dung đáp án không được để trống")
    private String text;
}
