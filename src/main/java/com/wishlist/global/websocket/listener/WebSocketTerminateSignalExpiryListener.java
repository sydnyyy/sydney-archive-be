package com.wishlist.global.websocket.listener;

import com.wishlist.global.websocket.enums.WebSocketSessionTerminateStatus;
import com.wishlist.global.websocket.manager.WebSocketSessionManager;
import com.wishlist.global.websocket.service.SystemEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import static com.wishlist.global.websocket.constant.WebSocketKeys.*;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketTerminateSignalExpiryListener implements MessageListener {

    private final WebSocketSessionManager webSocketSessionManager;
    private final SystemEventPublisher systemEventPublisher;

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> terminateSignalExpiryScript;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        log.info("🔑 TTL expired for key={}", expiredKey);

        if (expiredKey.startsWith(WEBSOCKET_SESSION_TERMINATE_SIGNAL_KEY_PREFIX)) {
            String clientId = expiredKey.substring(WEBSOCKET_SESSION_TERMINATE_SIGNAL_KEY_PREFIX.length());
            prepareTerminationSessions(clientId);
            sendTerminationCheckToClient(clientId);
        }
    }

    private void prepareTerminationSessions(String clientId) {
        String terminateStatusKey = WEBSOCKET_SESSION_TERMINATE_STATUS_PREFIX + clientId;

        redisTemplate.execute(
                terminateSignalExpiryScript,
                List.of(terminateStatusKey),
                WebSocketSessionTerminateStatus.PENDING.toString()
        );
    }

    private void sendTerminationCheckToClient(String clientId) {
        Set<String> sessions = Optional.ofNullable(webSocketSessionManager.getReadOnlyLocalSessions(clientId))
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
