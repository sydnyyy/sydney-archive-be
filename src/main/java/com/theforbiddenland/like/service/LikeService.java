package com.theforbiddenland.like.service;

import com.theforbiddenland.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final UserService userService;

    public void addLike(String sid, String itemId) {
        userService.addLike(sid, itemId);
    }

    public void deleteLike(String sid, String itemId) {
        userService.deleteLike(sid, itemId);
    }

    public List<String> getLikedItemIds(String sid) {
        return userService.getLikedItemIds(sid);
    }
}
