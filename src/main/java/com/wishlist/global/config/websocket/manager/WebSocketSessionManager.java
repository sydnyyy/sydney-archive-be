package com.wishlist.global.config.websocket.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class WebSocketSessionManager {

    private final StringRedisTemplate redisTemplate;
    private static final Duration TTL = Duration.ofMinutes(30);

    public void addSession(String clientId, String sessionId) {
        redisTemplate.opsForSet().add(clientId, sessionId);
        redisTemplate.expire(clientId, TTL);
    }

    public void updateTTL(String clientId) {
        redisTemplate.expire(clientId, TTL);
    }

    public void removeSession(String clientId, String sessionId) {
        redisTemplate.opsForSet().remove(clientId, sessionId);
    }
}
