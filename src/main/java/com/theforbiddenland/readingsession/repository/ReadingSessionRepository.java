package com.theforbiddenland.readingsession.repository;

import com.theforbiddenland.readingsession.entity.ReadingSession;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReadingSessionRepository extends MongoRepository<ReadingSession, String> {
}
