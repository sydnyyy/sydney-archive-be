package com.theforbiddenland.user.api;

import com.theforbiddenland.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserLikesController {

    private final LikeService likeService;

    @GetMapping("/{sid}/likes")
    public ResponseEntity<?> getUserLikes(@PathVariable String sid) {
        List<String> likedItemIds = likeService.getLikedItemIds(sid);
        return ResponseEntity.ok(likedItemIds);
    }
}
