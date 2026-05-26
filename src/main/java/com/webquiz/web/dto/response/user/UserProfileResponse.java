package com.webquiz.web.dto.response.user;

import com.webquiz.contact.enums.RoleType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {
    private String id;
    private String username;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private RoleType role;
}
