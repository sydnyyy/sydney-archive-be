package com.wishlist.global.websocket.listener;

import com.wishlist.global.websocket.manager.WebSocketSessionManager;
import com.wishlist.global.websocket.service.SystemEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import static com.wishlist.global.websocket.constant.WebSocketKeys.*;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketTerminateSignalExpiryListener implements MessageListener {

    private final WebSocketSessionManager webSocketSessionManager;
    private final SystemEventPublisher systemEventPublisher;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        log.info("[onMessage] TTL expired for key={}", expiredKey);

        if (expiredKey.startsWith(WEBSOCKET_SESSION_TERMINATE_SIGNAL_KEY_PREFIX)) {
            String clientId = expiredKey.substring(WEBSOCKET_SESSION_TERMINATE_SIGNAL_KEY_PREFIX.length());
            sendTerminationCheckToClient(clientId);
        }
    }

    private void sendTerminationCheckToClient(String clientId) {
        Set<String> sessions = Optional.ofNullable(webSocketSessionManager.getReadOnlyLocalSessions(clientId))
                .orElseGet(Set::of);

        log.info("[sendTerminationCheckToClient] Sending session termination event to {} sessions for clientId={}", sessions.size(), clientId);

        sessions.forEach(sessionId -> {
            try {
                systemEventPublisher.sendSessionTermination(clientId, sessionId, false);
            } catch (Exception e) {
                log.error("[sendTerminationCheckToClient] Failed to send termination event for clientId={}, sessionId={}", clientId, sessionId, e);
            }
        });
    }
}
