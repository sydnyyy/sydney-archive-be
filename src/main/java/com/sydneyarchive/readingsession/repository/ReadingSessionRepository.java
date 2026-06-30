package com.sydneyarchive.readingsession.repository;

import com.sydneyarchive.readingsession.entity.ReadingSession;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReadingSessionRepository extends MongoRepository<ReadingSession, String> {
}
