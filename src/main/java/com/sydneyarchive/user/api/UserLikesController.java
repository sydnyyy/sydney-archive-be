package com.sydneyarchive.user.api;

import com.sydneyarchive.user.service.UserLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/likes")
@RequiredArgsConstructor
public class UserLikesController {

    private final UserLikeService userLikeService;

    @GetMapping("/{uid}")
    public ResponseEntity<?> getUserLikes(@PathVariable String uid) {
        List<String> likedItemIds = userLikeService.getLikedItemIds(uid);
        return ResponseEntity.ok(likedItemIds);
    }

    @PostMapping("/{uid}/{itemId}")
    public ResponseEntity<?> addLike(
            @PathVariable String uid,
            @PathVariable String itemId
    ) {
        userLikeService.addLike(uid, itemId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{uid}/{itemId}")
    public ResponseEntity<?> deleteLike(
            @PathVariable String uid,
            @PathVariable String itemId
    ) {
        userLikeService.deleteLike(uid, itemId);
        return ResponseEntity.ok().build();
    }
}
