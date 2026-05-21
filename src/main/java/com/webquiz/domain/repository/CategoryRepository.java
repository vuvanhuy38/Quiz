package com.webquiz.domain.repository;

import com.webquiz.domain.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {

    boolean existsByNameAndParentId(String name, String parentId);

    List<Category> findByParentId(String parentId);

    List<Category> findByParentIdIsNull();

    List<Category> findByParentIdNotNull();

    @Query("{ $and: [ " +
            "  { parent_id: null }, " +
            "  { $or: [ { $expr: { $eq: [?0, ''] } }, { name: { $regex: ?0, $options: 'i' } }, { description: { $regex: ?0, $options: 'i' } } ] } " +
            "] }")
    Page<Category> findAllWithFilters(String name, Pageable pageable);
}
