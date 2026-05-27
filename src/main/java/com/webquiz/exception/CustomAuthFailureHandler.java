package com.webquiz.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomAuthFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        String message;

        if (exception instanceof DisabledException) {
            message = "Tài khoản đã bị vô hiệu hóa";
        } else if (exception instanceof UsernameNotFoundException
                   || exception instanceof BadCredentialsException) {
            message = "Tên đăng nhập hoặc mật khẩu không đúng";
        } else {
            message = "Đăng nhập thất bại";
        }

        response.sendRedirect("/login?error=" + URLEncoder.encode(message, StandardCharsets.UTF_8));
    }
}
