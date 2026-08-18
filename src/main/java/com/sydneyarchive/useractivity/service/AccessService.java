package com.sydneyarchive.useractivity.service;

import com.sydneyarchive.global.redis.constant.RedisKeys;
import com.sydneyarchive.global.redis.service.RedisStreamConsumer;
import com.sydneyarchive.global.websocket.manager.WebSocketSessionManager;
import com.sydneyarchive.useractivity.manager.UserAccessManager;
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

    public void recordAccess(String userId, String itemId) {
        if (webSocketSessionManager.hasSessionBySid(userId)) {
            webSocketSessionManager.updateTTL(userId);
            userAccessManager.recordAccess(userId);
        }
        broadcastUserActivity(userId);
    }

    private void broadcastUserActivity(String userId) {
        Map<String, String> message = new HashMap<>();
        message.put(RedisStreamConsumer.SID_FIELD, userId);
        redisTemplate.opsForStream().add(RedisKeys.USER_ACTIVITY_STREAM, message);
    }
}
