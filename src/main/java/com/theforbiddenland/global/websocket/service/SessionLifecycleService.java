package com.theforbiddenland.global.websocket.service;

import com.theforbiddenland.global.websocket.dto.SystemEventDto;
import com.theforbiddenland.global.websocket.manager.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.theforbiddenland.global.redis.constant.RedisKeys.*;

@Service
@RequiredArgsConstructor
public class SessionLifecycleService {

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> maintainSessionScript;
    private final DefaultRedisScript<Long> terminateSessionScript;
    private final WebSocketSessionManager webSocketSessionManager;

    public void processMaintain(SystemEventDto systemEvent) {
        String clientId = systemEvent.clientId();
        if (!webSocketSessionManager.hasSessionByClientId(clientId)) {
            return;
        }

        String terminateSignalKey = WS_TERMINATE_SIGNAL_KEY_PREFIX + clientId;
        String mainKey = WS_SESSION_MAIN_KEY_PREFIX + clientId;

        redisTemplate.execute(
                maintainSessionScript,
                List.of(WS_SESSION_TERMINATE_CHECK_ZSET, terminateSignalKey, mainKey),
                clientId,
                WebSocketSessionManager.TERMINATE_SIGNAL_KEY_DUMMY_VALUE,
                String.valueOf(WebSocketSessionManager.TERMINATE_SIGNAL_KEY_TTL.toSeconds()),
                String.valueOf(WebSocketSessionManager.MAIN_KEY_TTL.toSeconds())
        );
    }

    public void processTerminate(SystemEventDto systemEvent) {
        String clientId = systemEvent.clientId();
        if (!webSocketSessionManager.hasSessionByClientId(clientId)) {
            return;
        }

        redisTemplate.execute(
                terminateSessionScript,
                List.of(WS_SESSION_TERMINATE_CHECK_ZSET, WS_SESSION_TERMINATE_STREAM),
                clientId,
                String.valueOf(System.currentTimeMillis())
        );
    }
}
