package com.sydneyarchive.global.websocket.dto;

import com.sydneyarchive.global.websocket.interceptor.WebSocketHandshakeInterceptor;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.WebSocketSession;

@Builder
public record WebSocketSessionInfo(
        String sid,
        String role,
        String sessionId
) {
    public static WebSocketSessionInfo of(WebSocketSession session) {
        return WebSocketSessionInfo.builder()
                .sid(session.getAttributes().get(WebSocketHandshakeInterceptor.WS_ATTRIBUTE_SID_KEY).toString())
                .role(session.getAttributes().get(WebSocketHandshakeInterceptor.WS_ATTRIBUTE_ROLE_KEY).toString())
                .sessionId(session.getId())
                .build();
    }

    @NotNull
    @Override
    public String toString() {
        return String.format("sid=%s, role=%s, sessionId=%s", sid, role, sessionId);
    }
}
