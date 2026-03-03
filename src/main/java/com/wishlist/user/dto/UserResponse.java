package com.wishlist.user.dto;

import com.wishlist.user.entity.User;
import lombok.Builder;

import java.time.Instant;

@Builder
public record UserResponse (

        String uid,
        Instant lastMessageAt
) {

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .uid(user.getUid())
                .lastMessageAt(user.getLastMessageAt())
                .build();
    }
}
