package com.webquiz.web.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponsePage<T> extends Response<T> {

    private long totalElement;
    private int totalPage;
    private int pageSize;
    private int pageIndex;
}
