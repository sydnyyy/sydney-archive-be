package com.theforbiddenland.useractivity.service;

import com.theforbiddenland.global.redis.constant.RedisKeys;
import com.theforbiddenland.global.redis.service.RedisStreamConsumer;
import com.theforbiddenland.global.websocket.manager.WebSocketSessionManager;
import com.theforbiddenland.useractivity.entity.AccessEvent;
import com.theforbiddenland.useractivity.manager.UserAccessManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccessService {

    private final WebSocketSessionManager webSocketSessionManager;
    private final UserAccessManager userAccessManager;
    private final StringRedisTemplate redisTemplate;

    public void recordAccess(AccessEvent accessEvent) {
        if (webSocketSessionManager.hasSessionByClientId(accessEvent.uid())) {
            webSocketSessionManager.updateTTL(accessEvent.uid());
            userAccessManager.recordAccess(accessEvent.uid());
        }
        broadcastUserActivity(accessEvent);
    }

    private void broadcastUserActivity(AccessEvent accessEvent) {
        Map<String, String> message = new HashMap<>();
        message.put(RedisStreamConsumer.FIELD_CLIENT_ID, accessEvent.uid());
        redisTemplate.opsForStream().add(RedisKeys.USER_ACTIVITY_STREAM, message);
    }
}
