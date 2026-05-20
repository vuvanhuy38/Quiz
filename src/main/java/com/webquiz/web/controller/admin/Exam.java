package com.webquiz.web.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Exam {
    @GetMapping({"/admin/exams"})
    public String examManagement() {
        return "admin/exam-management";
    }

    @GetMapping({"/admin/exams/create"})
    public String examCreate() {
        return "admin/create-exam";
    }

    @GetMapping({"/admin/exams/update/{id}"})
    public String updateExamPage() {
        return "admin/create-exam";
    }

    @GetMapping({"/admin/questions"})
    public String questionsManagement() {
        return "questions-management";
    }
}
