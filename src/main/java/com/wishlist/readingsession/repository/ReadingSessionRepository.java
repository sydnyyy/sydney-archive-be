package com.wishlist.readingsession.repository;

import com.wishlist.readingsession.entity.ReadingSession;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReadingSessionRepository extends MongoRepository<ReadingSession, String> {
}
