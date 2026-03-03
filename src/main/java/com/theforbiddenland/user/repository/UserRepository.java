package com.theforbiddenland.user.repository;

import com.theforbiddenland.user.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUid(String uid);
    boolean existsByUid(String uid);
    List<User> findAllByOrderByLastMessageAtDesc();
    Optional<User> findByProviderAndProviderId(String provider, String providerId);
}
