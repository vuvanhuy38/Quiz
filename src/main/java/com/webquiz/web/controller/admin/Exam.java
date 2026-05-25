package com.webquiz.web.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Exam {
    @GetMapping("/admin/exams")
    public String examManagement() {
        return "admin/exam/exam-management";
    }

    @GetMapping("/admin/exams/create")
    public String examCreate() {
        return "admin/exam/create-exam";
    }

    @GetMapping("/admin/exams/update/{id}")
    public String updateExamPage() {
        return "admin/exam/create-exam";
    }

    @GetMapping("/admin/questions")
    public String questionsManagement() {
        return "admin/question/questions-management";
    }

    @GetMapping("/admin/questions/create")
    public String questionsCreate() {
        return "admin/question/create-question";
    }

    @GetMapping("/admin/questions/update/{id}")
    public String questionsUpdate() {
        return "admin/question/update-question";
    }

    @GetMapping("/admin/category")
    public String categoryManagement() {
        return "admin/category/category-management";
    }

    @GetMapping("/admin/users")
    public String userManagement() {
        return "admin/user/user-management";
    }
}
