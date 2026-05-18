package com.webquiz.domain.repository;

import com.webquiz.domain.entity.ExamAttempt;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExamAttemptRepository extends MongoRepository<ExamAttempt, String> {
}

