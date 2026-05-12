package com.theforbiddenland.global.websocket.dto;

import lombok.Builder;

@Builder
public record SystemEventDto (
        String type,
        String sid,
        String sessionId,
        boolean shouldTerminate
) {
}