package com.webquiz.domain.repository;

import com.webquiz.domain.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {

    boolean existsByNameAndParentId(String name, String parentId);

    Page<Category> findByParentIdIsNull(Pageable pageable);

    List<Category> findByParentId(String parentId);

    List<Category> findByParentIdIsNull();
}
