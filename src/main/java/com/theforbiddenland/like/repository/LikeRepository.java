package com.theforbiddenland.like.repository;

import com.theforbiddenland.like.entity.Like;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LikeRepository extends MongoRepository<Like, String> {

    void deleteByUserIdAndItemId(String userId, String itemId);

    List<Like> findByUserId(String userId);
}
