package com.webquiz.web.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Exam {
    @GetMapping({"/admin/exams"})
    public String dashboard() {
        return "admin/exam-management";
    }

    @GetMapping({"/admin/exams/create"})
    public String examCreate() {
        return "admin/create-exam";
    }
}
