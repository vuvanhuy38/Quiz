package com.webquiz.web.dto.response.exam;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExamOptionBankDto {

    private String key;

    private String text;
}
