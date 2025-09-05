package com.wishlist.user.dto;

import com.wishlist.user.entity.User;
import lombok.Builder;

import java.time.Instant;

@Builder
public record UserResponse (

        String userId,
        String clientId,
        Instant lastMessageAt
) {

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .clientId(user.getClientId())
                .lastMessageAt(user.getLastMessageAt())
                .build();
    }
}
