package com.wishlist.global.config.websocket.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketSessionManager {

    private final StringRedisTemplate redisTemplate;

    public void addSession(String clientId, String sessionId) {
        redisTemplate.opsForSet().add(clientId, sessionId);
    }

    public void removeSession(String clientId, String sessionId) {
        redisTemplate.opsForSet().remove(clientId, sessionId);
    }
}
