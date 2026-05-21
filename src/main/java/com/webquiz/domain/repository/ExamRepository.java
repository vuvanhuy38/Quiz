package com.webquiz.domain.repository;

import com.webquiz.contact.enums.StatusExamType;
import com.webquiz.domain.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends MongoRepository<Exam, String> {

    List<Exam> findTop10ByStatusOrderByAttemptCountDesc(StatusExamType status);

    List<Exam> findTop10ByStatusOrderByCreatedAtDesc(StatusExamType status);

    List<Exam> findByStatusAndTitleContainingIgnoreCase(StatusExamType status, String title);

    @Query("{ $and: [ " +
           "  { $or: [ { $expr: { $eq: [?0, ''] } }, { title: { $regex: ?0, $options: 'i' } } ] }, " +
           "  { $or: [ { $expr: { $eq: [?1, null] } }, { categoryId: ?1 } ] }, " +
           "  { $or: [ { $expr: { $eq: [?2, null] } }, { status: ?2 } ] } " +
           "] }")
    Page<Exam> findAllWithFilters(String title, String categoryId, StatusExamType status, Pageable pageable);
}
