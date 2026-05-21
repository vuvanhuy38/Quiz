package com.webquiz.web.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExamUser {
    @GetMapping("/user/do-exam/{id}")
    public String doExam() {
        return "user/do-exam";
    }

    @GetMapping("/user/result")
    public String resultExam() {
        return "user/result-exam";
    }
}
