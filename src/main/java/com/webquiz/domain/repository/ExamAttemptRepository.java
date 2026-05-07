package com.webquiz.domain.repository;

import com.webquiz.contact.enums.StatusExamType;
import com.webquiz.domain.entity.Exam;
import com.webquiz.domain.entity.ExamAttempt;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ExamAttemptRepository extends MongoRepository<ExamAttempt, String> {
}

