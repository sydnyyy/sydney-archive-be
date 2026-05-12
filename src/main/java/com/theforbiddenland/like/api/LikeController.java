package com.theforbiddenland.like.api;

import com.theforbiddenland.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{sid}/{itemId}")
    public ResponseEntity<?> addLike(@PathVariable String sid, @PathVariable String itemId) {
        likeService.addLike(sid, itemId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{sid}/{itemId}")
    public ResponseEntity<?> deleteLike(@PathVariable String sid, @PathVariable String itemId) {
        likeService.deleteLike(sid, itemId);
        return ResponseEntity.ok().build();
    }
}
