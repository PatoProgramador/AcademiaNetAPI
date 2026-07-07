package com.academianet.demo.repository.mongo;

import com.academianet.demo.document.UserPreferences;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserPreferencesRepository extends MongoRepository<UserPreferences, String> {
}
