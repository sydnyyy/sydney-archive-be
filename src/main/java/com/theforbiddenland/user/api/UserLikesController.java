package com.theforbiddenland.user.api;

import com.theforbiddenland.user.service.UserLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/likes")
@RequiredArgsConstructor
public class UserLikesController {

    private final UserLikeService userLikeService;

    @GetMapping("/{sid}")
    public ResponseEntity<?> getUserLikes(@PathVariable String sid) {
        List<String> likedItemIds = userLikeService.getLikedItemIds(sid);
        return ResponseEntity.ok(likedItemIds);
    }

    @PostMapping("/{sid}/{itemId}")
    public ResponseEntity<?> addLike(@PathVariable String sid, @PathVariable String itemId) {
        userLikeService.addLike(sid, itemId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{sid}/{itemId}")
    public ResponseEntity<?> deleteLike(@PathVariable String sid, @PathVariable String itemId) {
        userLikeService.deleteLike(sid, itemId);
        return ResponseEntity.ok().build();
    }
}
