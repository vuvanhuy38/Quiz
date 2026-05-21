package com.webquiz.web.dto.response.attempt;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StartAttemptResponse {

    private String attemptId;

    private LocalDateTime startedAt;

    private Integer timeLimit;
}
