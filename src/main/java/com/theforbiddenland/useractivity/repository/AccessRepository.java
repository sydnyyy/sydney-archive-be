package com.theforbiddenland.useractivity.repository;

import com.theforbiddenland.useractivity.entity.AccessEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccessRepository extends MongoRepository<AccessEvent, String> {
}
