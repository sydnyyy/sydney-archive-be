package com.wishlist.user.repository;

import com.wishlist.user.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    boolean existsByClientId(String clientId);
}
