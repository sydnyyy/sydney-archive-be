package com.wishlist.global.websocket.manager;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Component
public class WebSocketSessionCloser {

    @Retryable(
            retryFor = RuntimeException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void closeSession(WebSocketSession session, CloseStatus closeStatus) {
        if (session != null && session.isOpen()) {
            try {
                session.close(closeStatus);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
