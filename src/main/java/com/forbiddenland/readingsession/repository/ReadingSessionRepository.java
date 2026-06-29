package com.forbiddenland.readingsession.repository;

import com.forbiddenland.readingsession.entity.ReadingSession;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReadingSessionRepository extends MongoRepository<ReadingSession, String> {
}
