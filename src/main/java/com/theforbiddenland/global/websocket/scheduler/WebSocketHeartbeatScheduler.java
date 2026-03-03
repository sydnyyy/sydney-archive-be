package com.theforbiddenland.global.websocket.scheduler;

import com.theforbiddenland.global.websocket.handler.WebSocketSessionHandler;
import com.theforbiddenland.global.websocket.manager.WebSocketSessionCloser;
import com.theforbiddenland.global.websocket.manager.WebSocketSessionManager;
import com.theforbiddenland.global.websocket.service.WebSocketPingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collection;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHeartbeatScheduler {

    private static final Long PONG_TIMEOUT_MS = 60 * 2 * 1000L;
    private final WebSocketSessionManager webSocketSessionManager;
    private final WebSocketPingService webSocketPingService;
    private final WebSocketSessionCloser webSocketSessionCloser;

    @Scheduled(cron = "0 * * * * *")
    public void checkTimeoutAndSendPing() {
        Collection<WebSocketSession> sessions = webSocketSessionManager.getAllSessions();
        log.info("[WS Heartbeat Cycle] 타임아웃 검사 및 PING 발신 시작. 세션 수={}", sessions.size());

        long now = System.currentTimeMillis();
        sessions.parallelStream().forEach(session -> {
            Long lastActiveTime = (Long) session.getAttributes().get(WebSocketSessionHandler.WS_LAST_HEARTBEAT_TIME);
            if (lastActiveTime == null || (now - lastActiveTime > PONG_TIMEOUT_MS)) {
                log.warn("[WS Heartbeat 타임아웃 감지] WebSocket session 강제 종료 시작. sessionId={}", session.getId());
                webSocketSessionCloser.closeSession(session, new CloseStatus(4001, "Pong timeout"));
                return;
            }

            webSocketPingService.sendPing(session);
        });
    }
}
