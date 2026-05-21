package com.webquiz.domain.repository;

import com.webquiz.contact.enums.StatusUserType;
import com.webquiz.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User findByUsernameAndStatus(String username, StatusUserType status);

    @Query("{ $and: [ " +
           " { $or: [ { $expr: { $eq: [?0, ''] } }, { 'first_name': { $regex: ?0, $options: 'i' } }, { 'last_name': { $regex: ?0, $options: 'i' } } ] }, " +
           " { $or: [ { $expr: { $eq: [?1, ''] } }, { 'phone': { $regex: ?1, $options: 'i' } } ] }, " +
           " { $or: [ { $expr: { $eq: [?2, ''] } }, { 'email': { $regex: ?2, $options: 'i' } } ] } " +
           "] }")
    Page<User> findAllWithFilters(String name, String phone, String email, Pageable pageable);
}
