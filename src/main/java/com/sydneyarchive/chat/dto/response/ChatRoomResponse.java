package com.sydneyarchive.chat.dto.response;

import com.sydneyarchive.user.dto.internal.UserContext;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ChatRoomResponse(
        String chatRoomId,
        Instant lastMessageAt
) {

    public static ChatRoomResponse of(UserContext userContext) {
        return ChatRoomResponse.builder()
                .chatRoomId(userContext.userSid())
                .lastMessageAt(userContext.lastMessageAt())
                .build();
    }
}
