package com.sydneyarchive.user.dto.internal;

import com.sydneyarchive.user.entity.User;
import lombok.Builder;

import java.time.Instant;

@Builder
public record UserContext(
        String userId,
        Instant lastMessageAt
) {

    public static UserContext of(User user) {
        return UserContext.builder()
                .userId(user.getId())
                .lastMessageAt(user.getLastMessageAt())
                .build();
    }
}
