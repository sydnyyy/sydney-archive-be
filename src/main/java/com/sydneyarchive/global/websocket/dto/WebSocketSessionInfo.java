package com.sydneyarchive.global.websocket.dto;

import com.sydneyarchive.global.websocket.interceptor.WebSocketHandshakeInterceptor;
import org.springframework.web.socket.WebSocketSession;

public record WebSocketSessionInfo(
        String sid,
        String sessionId
) {
    public static WebSocketSessionInfo of(WebSocketSession session) {
        return new WebSocketSessionInfo(
                session.getAttributes().get(WebSocketHandshakeInterceptor.SID_KEY).toString(),
                session.getId()
        );
    }

    @Override
    public String toString() {
        return String.format(
                "sid=%s, sessionId=%s",
                sid, sessionId);
    }
}
