package com.wishlist.global.websocket.scheduler;

import com.wishlist.global.websocket.manager.WebSocketSessionManager;
import com.wishlist.global.websocket.service.WebSocketPingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collection;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketPingScheduler {

    private final WebSocketSessionManager webSocketSessionManager;
    private final WebSocketPingService webSocketPingService;

    @Scheduled(cron = "0 * * * * *")
    public void sendPingToAllSessions() {
        Collection<WebSocketSession> sessions = webSocketSessionManager.getAllSessions();
        log.info("[WS Scheduler] PING broadcast 시작. 세션 수={}", sessions.size());
        if (!sessions.isEmpty()) {
            sessions.parallelStream().forEach(webSocketPingService::sendPing);
        }
    }
}
