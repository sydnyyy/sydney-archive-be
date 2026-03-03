package com.wishlist.user.api;

import com.wishlist.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserLikesController {

    private final LikeService likeService;

    @GetMapping("/{uid}/likes")
    public ResponseEntity<?> getUserLikes(@PathVariable String uid) {
        Set<String> likedItemIds = likeService.getLikedItemIds(uid);
        return ResponseEntity.ok(likedItemIds);
    }
}
