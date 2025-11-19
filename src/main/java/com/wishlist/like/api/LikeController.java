package com.wishlist.like.api;

import com.wishlist.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{clientId}/{itemId}")
    public ResponseEntity<?> addLike(@PathVariable String clientId, @PathVariable String itemId) {
        likeService.addLike(clientId, itemId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{clientId}/{itemId}")
    public ResponseEntity<?> deleteLike(@PathVariable String clientId, @PathVariable String itemId) {
        likeService.deleteLike(clientId, itemId);
        return ResponseEntity.ok().build();
    }
}
