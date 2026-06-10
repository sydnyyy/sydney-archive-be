package com.theforbiddenland.devlog.repository;

import com.theforbiddenland.devlog.entity.DevLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DevLogRepository extends MongoRepository<DevLog, String> {
}
