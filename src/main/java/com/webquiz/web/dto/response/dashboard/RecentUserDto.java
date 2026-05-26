package com.webquiz.web.dto.response.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecentUserDto {

    private String id;
    private String username;
    private String fullName;
    private String email;
    private String createdAt;
    private String status;
}
