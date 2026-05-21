package com.webquiz.web.dto.response.user;


import com.webquiz.contact.enums.RoleType;
import com.webquiz.contact.enums.StatusUserType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserItemResponse {

    private String id;
    private String username;
    private String email;
    private String phone;
    private String fullName;
    private RoleType role;
    private StatusUserType status;
}
