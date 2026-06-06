package com.theforbiddenland.user.service;

import com.theforbiddenland.global.exception.ErrorCode;
import com.theforbiddenland.global.exception.UserException;
import com.theforbiddenland.user.entity.User;
import com.theforbiddenland.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserLikeService {

    private final UserRepository userRepository;

    public void addLike(String sid, String itemId) {
        User user = userRepository.findBySid(sid)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        user.addLikedItemId(itemId);
        userRepository.save(user);
    }

    public void deleteLike(String sid, String itemId) {
        User user = userRepository.findBySid(sid)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        user.deleteLikeItemId(itemId);
        userRepository.save(user);
    }

    public List<String> getLikedItemIds(String sid) {
        User user = userRepository.findBySid(sid)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        List<String> likedItemIds = new ArrayList<>(user.getLikedItemIds());
        Collections.reverse(likedItemIds);
        return likedItemIds;
    }
}
