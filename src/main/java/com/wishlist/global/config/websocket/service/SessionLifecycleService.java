package com.wishlist.global.config.websocket.service;

import com.wishlist.global.config.websocket.dto.SystemEventDto;
import com.wishlist.global.config.websocket.enums.WebSocketSessionTerminateStatus;
import com.wishlist.global.config.websocket.manager.WebSocketSessionManager;
import com.wishlist.global.config.websocket.manager.WebSocketTerminateSignalExpiryListener;
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

    public void processMaintain(SystemEventDto systemEvent) {
        String terminateStatusKey = getTerminateStatusKey(systemEvent.clientId());
        String terminateSignalKey = getTerminateSignalKey(systemEvent.clientId());

        redisTemplate.execute(
                maintainSessionScript,
                List.of(terminateStatusKey, terminateSignalKey),
                WebSocketSessionTerminateStatus.ACTIVE.toString(),
                WebSocketSessionManager.TERMINATE_SIGNAL_KEY_DUMMY_VALUE,
                String.valueOf(WebSocketSessionManager.TERMINATE_SIGNAL_KEY_TTL.toSeconds())
        );
    }

    private String getTerminateStatusKey(String clientId) {
        return WebSocketTerminateSignalExpiryListener.WEBSOCKET_SESSION_TERMINATE_STATUS_PREFIX + clientId;
    }

    private String getTerminateSignalKey(String clientId) {
        return WebSocketSessionManager.WEBSOCKET_SESSION_TERMINATE_SIGNAL_KEY_PREFIX + clientId;
    }
}
