package com.wishlist.global.websocket.service;

import com.wishlist.global.redis.service.RedisStreamConsumer;
import com.wishlist.global.websocket.dto.SystemEventDto;
import com.wishlist.global.websocket.enums.WebSocketSessionTerminateStatus;
import com.wishlist.global.websocket.manager.WebSocketSessionManager;
import com.wishlist.global.websocket.listener.WebSocketTerminateSignalExpiryListener;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionLifecycleService {

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> maintainSessionScript;
    private final DefaultRedisScript<Long> terminateSessionScript;
    private final WebSocketSessionManager webSocketSessionManager;

    public void processMaintain(SystemEventDto systemEvent) {
        String clientId = systemEvent.clientId();
        if (!webSocketSessionManager.hasSession(clientId)) {
            return;
        }

        String terminateStatusKey = getTerminateStatusKey(clientId);
        String terminateSignalKey = getTerminateSignalKey(clientId);
        String mainKey = getMainKey(clientId);

        redisTemplate.execute(
                maintainSessionScript,
                List.of(terminateStatusKey, terminateSignalKey, mainKey),
                WebSocketSessionTerminateStatus.ACTIVE.toString(),
                WebSocketSessionManager.TERMINATE_SIGNAL_KEY_DUMMY_VALUE,
                String.valueOf(WebSocketSessionManager.TERMINATE_SIGNAL_KEY_TTL.toSeconds()),
                String.valueOf(WebSocketSessionManager.MAIN_KEY_TTL.toSeconds())
        );
    }

    public void processTerminate(SystemEventDto systemEvent) {
        String clientId = systemEvent.clientId();
        if (!webSocketSessionManager.hasSession(clientId)) {
            return;
        }

        String terminateStatusKey = getTerminateStatusKey(clientId);
        String terminateSignalKey = getTerminateSignalKey(clientId);
        String activeMainKey = getMainKey(clientId);
        String terminatedSessionsZSetKey = getTerminateSessionsKey(clientId);
        String terminateSessionStreamKey = RedisStreamConsumer.STREAM_TERMINATE_SESSION;

        redisTemplate.execute(
                terminateSessionScript,
                List.of(terminateStatusKey, terminateSignalKey,
                        activeMainKey, terminatedSessionsZSetKey, terminateSessionStreamKey),
                WebSocketSessionTerminateStatus.TERMINATE.toString(),
                clientId,
                String.valueOf(System.currentTimeMillis())
        );
    }

    private String getTerminateStatusKey(String clientId) {
        return WebSocketTerminateSignalExpiryListener.WEBSOCKET_SESSION_TERMINATE_STATUS_PREFIX + clientId;
    }

    private String getTerminateSignalKey(String clientId) {
        return WebSocketSessionManager.WEBSOCKET_SESSION_TERMINATE_SIGNAL_KEY_PREFIX + clientId;
    }

    private String getMainKey(String clientId) {
        return WebSocketSessionManager.WEBSOCKET_SESSION_MAIN_KEY_PREFIX + clientId;
    }

    private String getTerminateSessionsKey(String clientId) {
        return WebSocketTerminateSignalExpiryListener.WEBSOCKET_SESSION_TERMINATE_SESSIONS_PREFIX + clientId;
    }
}
