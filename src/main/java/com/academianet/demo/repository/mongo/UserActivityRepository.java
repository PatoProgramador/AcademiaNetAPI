package com.academianet.demo.repository.mongo;

import com.academianet.demo.document.UserActivity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserActivityRepository extends MongoRepository<UserActivity, String> {

    List<UserActivity> findByUserIdOrderByTimestampDesc(String userId, Pageable pageable);
}
