package com.wishlist.global.config.websocket.manager;

import com.wishlist.global.config.websocket.service.SystemEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketSentinelExpiryListener implements MessageListener {

    private final WebSocketSessionManager webSocketSessionManager;
    private final SystemEventPublisher systemEventPublisher;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        log.info("🔑 TTL expired for key={}", expiredKey);

        if (expiredKey.startsWith(WebSocketSessionManager.WEBSOCKET_SESSION_SENTINEL_KEY_PREFIX)) {
            String clientId = expiredKey.substring(WebSocketSessionManager.WEBSOCKET_SESSION_SENTINEL_KEY_PREFIX.length());
            Set<String> sessions = Optional.ofNullable(webSocketSessionManager.getSessions(clientId))
                    .orElseGet(Set::of);

            log.info("📧 Sending session termination event to {} sessions for clientId={}", sessions.size(), clientId);

            sessions.forEach(sessionId -> {
                try {
                    systemEventPublisher.sendSessionTermination(clientId, sessionId, false);
                } catch (Exception e) {
                    log.error("⚠️ Failed to send termination event for clientId={}, sessionId={}", clientId, sessionId, e);
                }
            });
        }
    }
}
