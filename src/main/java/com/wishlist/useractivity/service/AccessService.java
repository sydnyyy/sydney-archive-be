package com.wishlist.useractivity.service;

import com.wishlist.global.redis.constant.RedisKeys;
import com.wishlist.global.redis.service.RedisStreamConsumer;
import com.wishlist.global.websocket.manager.WebSocketSessionManager;
import com.wishlist.useractivity.entity.AccessEvent;
import com.wishlist.useractivity.manager.UserAccessManager;
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
        if (webSocketSessionManager.hasSessionByUid(accessEvent.uid())) {
            webSocketSessionManager.updateTTL(accessEvent.uid());
            userAccessManager.recordAccess(accessEvent.uid());
        }
        broadcastUserActivity(accessEvent);
    }

    private void broadcastUserActivity(AccessEvent accessEvent) {
        Map<String, String> message = new HashMap<>();
        message.put(RedisStreamConsumer.FIELD_UID, accessEvent.uid());
        redisTemplate.opsForStream().add(RedisKeys.USER_ACTIVITY_STREAM, message);
    }
}
