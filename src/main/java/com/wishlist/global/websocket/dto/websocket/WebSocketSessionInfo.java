package com.wishlist.global.websocket.dto.websocket;

import com.wishlist.global.websocket.interceptor.WebSocketHandshakeInterceptor;
import org.springframework.web.socket.WebSocketSession;

public record WebSocketSessionInfo(
        String userId,
        String clientId,
        String sessionId
) {
    public static WebSocketSessionInfo of(WebSocketSession session) {
        return new WebSocketSessionInfo(
                "UNKNOWN",
                session.getAttributes().get(WebSocketHandshakeInterceptor.CLIENT_ID_KEY).toString(),
                session.getId()
        );
    }

    @Override
    public String toString() {
        return String.format(
                "userId=%s, clientId=%s, sessionId=%s",
                userId, clientId, sessionId);
    }
}
