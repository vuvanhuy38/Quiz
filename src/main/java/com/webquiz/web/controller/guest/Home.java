package com.webquiz.web.controller.guest;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Home {
    @GetMapping({"/", "/home"})
    public String home() {
        return "guest/home";
    }

    @GetMapping({"/detail/{id}"})
    public String examDeati() {
        return "guest/exam-detail";
    }

    @GetMapping("/handle-login-success")
    public String loginSuccess(HttpServletRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))) {

            return "redirect:/admin/dashboard";
        }

        return "redirect:/home";
    }
}
