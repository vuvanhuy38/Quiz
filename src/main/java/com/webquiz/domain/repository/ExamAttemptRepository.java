package com.webquiz.domain.repository;

import com.webquiz.domain.entity.ExamAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExamAttemptRepository extends MongoRepository<ExamAttempt, String> {

    @Query("{ $and: [ " +
           "  { userId: ?0 }, " +
           "  { $or: [ { $expr: { $eq: [?1, false] } }, { examId: { $in: ?2 } } ] }, " +
           "  { $or: [ { $expr: { $eq: [?3, null] } }, { startedAt: { $gte: ?3 } } ] }, " +
           "  { $or: [ { $expr: { $eq: [?4, null] } }, { startedAt: { $lte: ?4 } } ] } " +
           "] }")
    Page<ExamAttempt> findAllWithFilters(String userId,
                                         boolean filterByExam,
                                         List<String> examIds,
                                         LocalDateTime startDateFrom,
                                         LocalDateTime startDateTo,
                                         Pageable pageable);
}
