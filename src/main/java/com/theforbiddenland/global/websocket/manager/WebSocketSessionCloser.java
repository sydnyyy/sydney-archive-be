package com.theforbiddenland.global.websocket.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Component
@Slf4j
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

    @Recover
    public void recover(RuntimeException e, WebSocketSession session, CloseStatus closeStatus) {
        log.error("[WS Close Retry] 세션 종료 실패 (3회 시도 후 중단): sessionId={}, reason={}",
                session != null ? session.getId() : "unknown", e.getMessage());

        try {
            if (session != null && session.isOpen()) {
                session.close(CloseStatus.SERVER_ERROR);
            }
        } catch (IOException ignored) {
            // TODO: 경고 알림
        }
    }
}
