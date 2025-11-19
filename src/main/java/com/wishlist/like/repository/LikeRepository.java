package com.wishlist.like.repository;

import com.wishlist.like.entity.Like;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LikeRepository extends MongoRepository<Like, String> {

    void deleteByUserIdAndItemId(String userId, String itemId);
}
