package com.webquiz.domain.repository;

import com.webquiz.domain.entity.ExamQuestion;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamQuestionRepository extends MongoRepository<ExamQuestion, String> {

    long countByExamId(String examId);

    List<ExamQuestion> findByExamId(String examId);
}
