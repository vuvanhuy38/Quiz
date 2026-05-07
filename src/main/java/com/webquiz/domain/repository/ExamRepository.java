package com.webquiz.domain.repository;

import com.webquiz.contact.enums.StatusExamType;
import com.webquiz.domain.entity.Exam;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends MongoRepository<Exam, String> {

    List<Exam> findTop10ByStatusOrderByAttemptCountDesc(StatusExamType status);

    @Query(value = "{'status': ?0}", sort = "{'createdAt': -1 }")
    List<Exam> findTop10ByStatusOrderByCreatedAtDesc(StatusExamType status);

    @Query("{'status': ?0, 'title': { $regex: ?1, $options: 'i'}}")
    List<Exam> findByStatusAndTitleContainingIgnoreCase(StatusExamType status, String title);
}
