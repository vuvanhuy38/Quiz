package com.webquiz.contact;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SessionUtil {

    public static UserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalArgumentException("user not authenticated");
        }
        // lấy thông tin user đang đăng nhập
        UserDetails currentUser = (UserDetails) authentication.getPrincipal();
        return currentUser;
    }
}
