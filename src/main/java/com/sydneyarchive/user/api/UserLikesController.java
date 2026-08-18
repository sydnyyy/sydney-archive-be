package com.sydneyarchive.user.api;

import com.sydneyarchive.auth.security.UserPrincipal;
import com.sydneyarchive.user.service.UserLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/g/likes")
@RequiredArgsConstructor
public class UserLikesController {

    private final UserLikeService userLikeService;

    @GetMapping
    public ResponseEntity<?> getUserLikes(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        List<String> likedItemIds = userLikeService.getLikedItemIds(userPrincipal.getUserId());
        return ResponseEntity.ok(likedItemIds);
    }

    @PostMapping("/{itemId}")
    public ResponseEntity<?> addLike(
            @PathVariable String itemId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        userLikeService.addLike(userPrincipal.getUserId(), itemId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteLike(
            @PathVariable String itemId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        userLikeService.deleteLike(userPrincipal.getUserId(), itemId);
        return ResponseEntity.ok().build();
    }
}
