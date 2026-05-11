package com.webquiz.web.dto.response.questionBank;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OptionBankResponse {

    private String key;

    private String text;
}
