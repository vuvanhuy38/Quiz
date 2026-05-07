package com.webquiz.domain.repository;

import com.webquiz.domain.entity.QuestionBank;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionBankRepository extends MongoRepository<QuestionBank, String> {

    void deleteByCategoryId(String categoryId);

    void deleteByParentCategoryId(String parentCategoryId);
}
