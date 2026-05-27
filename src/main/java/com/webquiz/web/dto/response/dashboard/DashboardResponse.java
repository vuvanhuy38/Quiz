package com.webquiz.web.dto.response.dashboard;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardResponse {

    private long totalUsers;
    private long totalExams;
    private long totalCategories;
    private long totalAttempts;
    private long totalQuestions;

    private List<RecentAttemptDto> recentAttempts;

    private List<String> trendLabels;
    private List<Long> trendData;
}
