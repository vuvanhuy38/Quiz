package com.webquiz.domain.repository;

import com.webquiz.domain.entity.QuestionBank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionBankRepository extends MongoRepository<QuestionBank, String> {
    void deleteByCategoryId(String categoryId);

    @Query("{ $and: [ " +
           "  { $or: [ { $expr: { $eq: [?0, null] } }, { type: ?0 } ] }, " +
           "  { $or: [ { $expr: { $eq: [?1, null] } }, { level: ?1 } ] }, " +
           "  { $or: [ { $expr: { $eq: [?2, ''] } }, { content: { $regex: ?2, $options: 'i' } } ] }, " +
           "  { $or: [ { $expr: { $eq: [?3, null] } }, { categoryId: ?3 } ] } " +
           "] }")
    Page<QuestionBank> findAllWithFilters(String type, String level, String content, String categoryId,
                                          Pageable pageable);
}
