package com.wishlist.global.config.websocket.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class WebSocketSessionManager {

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> addSessionScript;
    private final DefaultRedisScript<Long> updateTTLScript;
    private final DefaultRedisScript<Long> removeSessionScript;

    private static final String WEBSOCKET_SESSION_MAIN_KEY_PREFIX = "WS:MAIN:";
    public static final String WEBSOCKET_SESSION_SENTINEL_KEY_PREFIX = "WS:SENTINEL:";
    private static final Duration MAIN_KEY_TTL = Duration.ofMinutes(30);
    private static final Duration SENTINEL_KEY_TTL = Duration.ofMinutes(1);
    private static final String SENTINEL_DUMMY_VALUE = "1";

    public void addSession(String clientId, String sessionId) {
        String mainKey = WEBSOCKET_SESSION_MAIN_KEY_PREFIX + clientId;
        String sentinelKey = WEBSOCKET_SESSION_SENTINEL_KEY_PREFIX + clientId;

        redisTemplate.execute(
                addSessionScript,
                List.of(mainKey, sentinelKey),
                sessionId,
                String.valueOf(MAIN_KEY_TTL.toSeconds()),
                SENTINEL_DUMMY_VALUE,
                String.valueOf(SENTINEL_KEY_TTL.toSeconds())
        );
    }

    public void updateTTL(String clientId) {
        String mainKey = WEBSOCKET_SESSION_MAIN_KEY_PREFIX + clientId;
        String sentinelKey = WEBSOCKET_SESSION_SENTINEL_KEY_PREFIX + clientId;

        redisTemplate.execute(
                updateTTLScript,
                List.of(mainKey, sentinelKey),
                String.valueOf(MAIN_KEY_TTL.toSeconds()),
                String.valueOf(SENTINEL_KEY_TTL.toSeconds())
        );
    }

    public void removeSession(String clientId, String sessionId) {
        String mainKey = WEBSOCKET_SESSION_MAIN_KEY_PREFIX + clientId;
        String sentinelKey = WEBSOCKET_SESSION_SENTINEL_KEY_PREFIX + clientId;

        redisTemplate.execute(
                removeSessionScript,
                List.of(mainKey, sentinelKey),
                sessionId
        );
    }

    public Set<String> getSessions(String clientId) {
        return redisTemplate.opsForSet().members(WEBSOCKET_SESSION_MAIN_KEY_PREFIX + clientId);
    }
}
