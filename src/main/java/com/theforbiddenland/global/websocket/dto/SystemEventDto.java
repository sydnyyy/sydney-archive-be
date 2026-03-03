package com.theforbiddenland.global.websocket.dto;

import lombok.Builder;

@Builder
public record SystemEventDto (
        String type,
        String clientId,
        String sessionId,
        boolean shouldTerminate
) {
}