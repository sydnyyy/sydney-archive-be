package com.sydneyarchive.user.dto;

import com.sydneyarchive.user.entity.User;
import lombok.Builder;

import java.time.Instant;

@Builder
public record UserResponse (

        String sid,
        Instant lastMessageAt
) {

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .sid(user.getSid())
                .lastMessageAt(user.getLastMessageAt())
                .build();
    }
}
