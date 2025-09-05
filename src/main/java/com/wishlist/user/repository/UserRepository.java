package com.wishlist.user.repository;

import com.wishlist.user.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByClientId(String clientId);
    boolean existsByClientId(String clientId);
    List<User> findAllByOrderByLastMessageAtDesc();
}
