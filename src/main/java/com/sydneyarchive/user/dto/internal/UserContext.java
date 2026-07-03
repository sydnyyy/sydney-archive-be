package com.sydneyarchive.user.dto.internal;

import com.sydneyarchive.user.entity.User;
import lombok.Builder;

import java.time.Instant;

@Builder
public record UserContext(

        String userSid,
        Instant lastMessageAt
) {

    public static UserContext of(User user) {
        return UserContext.builder()
                .userSid(user.getSid())
                .lastMessageAt(user.getLastMessageAt())
                .build();
    }
}
