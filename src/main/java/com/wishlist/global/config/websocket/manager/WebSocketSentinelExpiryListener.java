package com.wishlist.global.config.websocket.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketSentinelExpiryListener implements MessageListener {

    private final WebSocketSessionManager webSocketSessionManager;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredSentinelKey = message.toString();
        log.info("🔑 Sentinel TTL expired for expiredKey={}", expiredSentinelKey);

        // TODO: 종료 알림 전송
    }
}
