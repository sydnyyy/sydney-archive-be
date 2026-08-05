package com.sydneyarchive.user.dto.internal;

import com.sydneyarchive.user.entity.User;
import lombok.Builder;

import java.time.Instant;

@Builder
public record UserContext(

        String uid,
        Instant lastMessageAt
) {

    public static UserContext of(User user) {
        return UserContext.builder()
                .uid(user.getUid())
                .lastMessageAt(user.getLastMessageAt())
                .build();
    }
}
