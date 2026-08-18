package com.sydneyarchive.user.service;

import com.sydneyarchive.global.exception.ErrorCode;
import com.sydneyarchive.global.exception.UserException;
import com.sydneyarchive.user.entity.User;
import com.sydneyarchive.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserLikeService {

    private final UserRepository userRepository;

    public List<String> getLikedItemIds(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        List<String> likedItemIds = new ArrayList<>(user.getLikedItemIds());
        Collections.reverse(likedItemIds);
        return likedItemIds;
    }

    public void addLike(String userId, String itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        user.addLikedItemId(itemId);
        userRepository.save(user);
    }

    public void deleteLike(String userId, String itemId) {
        userRepository.findById(userId)
                .ifPresent(user -> {
                    user.deleteLikeItemId(itemId);
                    userRepository.save(user);
                });
    }
}
