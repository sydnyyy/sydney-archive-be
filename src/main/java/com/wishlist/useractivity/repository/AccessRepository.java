package com.wishlist.useractivity.repository;

import com.wishlist.useractivity.entity.AccessEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccessRepository extends MongoRepository<AccessEvent, String> {
}
