package com.sydneyarchive.useractivity.service;

import com.sydneyarchive.global.redis.constant.RedisKeys;
import com.sydneyarchive.global.redis.service.RedisStreamConsumer;
import com.sydneyarchive.global.websocket.manager.WebSocketSessionManager;
import com.sydneyarchive.useractivity.entity.AccessEvent;
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

    public void recordAccess(AccessEvent accessEvent) {
        if (webSocketSessionManager.hasSessionBySid(accessEvent.uid())) {
            webSocketSessionManager.updateTTL(accessEvent.uid());
            userAccessManager.recordAccess(accessEvent.uid());
        }
        broadcastUserActivity(accessEvent);
    }

    private void broadcastUserActivity(AccessEvent accessEvent) {
        Map<String, String> message = new HashMap<>();
        message.put(RedisStreamConsumer.SID_FIELD, accessEvent.uid());
        redisTemplate.opsForStream().add(RedisKeys.USER_ACTIVITY_STREAM, message);
    }
}
