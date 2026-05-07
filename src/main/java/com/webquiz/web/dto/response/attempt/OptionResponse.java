package com.webquiz.web.dto.response.attempt;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OptionResponse {
    private String key;

    private String text;
}
