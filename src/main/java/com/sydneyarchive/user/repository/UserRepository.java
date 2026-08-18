package com.sydneyarchive.user.repository;

import com.sydneyarchive.user.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    List<User> findAllByLastMessageAtIsNotNullOrderByLastMessageAtDesc();
    Optional<User> findByProviderAndProviderId(String provider, String providerId);
}
