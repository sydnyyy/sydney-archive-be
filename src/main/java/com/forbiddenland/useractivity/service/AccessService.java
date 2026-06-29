package com.forbiddenland.useractivity.service;

import com.forbiddenland.global.redis.constant.RedisKeys;
import com.forbiddenland.global.redis.service.RedisStreamConsumer;
import com.forbiddenland.global.websocket.manager.WebSocketSessionManager;
import com.forbiddenland.useractivity.entity.AccessEvent;
import com.forbiddenland.useractivity.manager.UserAccessManager;
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
        if (webSocketSessionManager.hasSessionBySid(accessEvent.sid())) {
            webSocketSessionManager.updateTTL(accessEvent.sid());
            userAccessManager.recordAccess(accessEvent.sid());
        }
        broadcastUserActivity(accessEvent);
    }

    private void broadcastUserActivity(AccessEvent accessEvent) {
        Map<String, String> message = new HashMap<>();
        message.put(RedisStreamConsumer.SID_FIELD, accessEvent.sid());
        redisTemplate.opsForStream().add(RedisKeys.USER_ACTIVITY_STREAM, message);
    }
}
