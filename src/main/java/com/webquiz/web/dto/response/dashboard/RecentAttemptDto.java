package com.webquiz.web.dto.response.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecentAttemptDto {

    private String id;
    private String studentName;
    private String examTitle;
    private Double score;
    private String status;
    private String time;
}
