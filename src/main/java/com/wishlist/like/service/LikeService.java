package com.wishlist.like.service;

import com.mongodb.DuplicateKeyException;
import com.wishlist.like.entity.Like;
import com.wishlist.like.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {

    private final LikeRepository likeRepository;

    public void addLike(String userId, String itemId) {
        Like like = Like.of(userId, itemId);
        try {
            likeRepository.save(like);
        } catch (DuplicateKeyException e) {
            log.warn("Like already exists for userId={} itemId={}", userId, itemId);
        }
    }

    public void deleteLike(String userId, String itemId) {
        likeRepository.deleteByUserIdAndItemId(userId, itemId);
    }
}
