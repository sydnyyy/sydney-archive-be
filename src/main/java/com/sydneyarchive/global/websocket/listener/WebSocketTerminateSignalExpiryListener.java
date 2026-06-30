package com.sydneyarchive.global.websocket.listener;

import com.sydneyarchive.global.websocket.manager.WebSocketSessionManager;
import com.sydneyarchive.global.websocket.service.SystemEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import static com.sydneyarchive.global.redis.constant.RedisKeys.*;

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

        if (expiredKey.startsWith(WS_TERMINATE_SIGNAL_KEY_PREFIX)) {
            String sid = expiredKey.substring(WS_TERMINATE_SIGNAL_KEY_PREFIX.length());
            sendTerminationCheckToClient(sid);
        }
    }

    private void sendTerminationCheckToClient(String sid) {
        Set<String> sessions = Optional.ofNullable(webSocketSessionManager.getReadOnlyLocalSessions(sid))
                .orElseGet(Set::of);

        log.info("[sendTerminationCheckToClient] Sending session termination event to {} sessions for sid={}", sessions.size(), sid);

        sessions.forEach(sessionId -> {
            try {
                systemEventPublisher.sendSessionTermination(sid, sessionId, false);
            } catch (Exception e) {
                log.error("[sendTerminationCheckToClient] Failed to send termination event for sid={}, sessionId={}", sid, sessionId, e);
            }
        });
    }
}
