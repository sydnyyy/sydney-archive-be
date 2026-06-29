package com.forbiddenland.user.repository;

import com.forbiddenland.user.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findBySid(String sid);
    boolean existsBySid(String sid);
    List<User> findAllByOrderByLastMessageAtDesc();
    Optional<User> findByProviderAndProviderId(String provider, String providerId);
}
