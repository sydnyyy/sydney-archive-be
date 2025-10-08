package com.wishlist.global.redis.service;

import com.wishlist.global.websocket.manager.WebSocketSessionManager;
import com.wishlist.useractivity.manager.UserAccessManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.wishlist.global.redis.constant.RedisKeys.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisStreamConsumer {

    public static final String STREAM_OTHER_EVENT = "other_event_stream";
    public static final String FIELD_CLIENT_ID = "clientId";

    private final WebSocketSessionManager webSocketSessionManager;
    private final UserAccessManager userAccessManager;
    private final StringRedisTemplate redisTemplate;
    private static final Map<String, String> lastIds = new ConcurrentHashMap<>();

    static {
        lastIds.put(WS_SESSION_TERMINATE_STREAM, "0");
        lastIds.put(USER_ACTIVITY_STREAM, "0");
        lastIds.put(STREAM_OTHER_EVENT, "0");
    }

    @Scheduled(cron = "0 * * * * *")
    public void pollStreams() {
        List<MapRecord<String, Object, Object>> messages = redisTemplate.opsForStream()
                .read(StreamReadOptions.empty().count(100),
                        lastIds.entrySet().stream()
                                .map(e -> StreamOffset.create(e.getKey(), ReadOffset.from(e.getValue())))
                                .toArray(StreamOffset[]::new)
                );

        if (messages == null || messages.isEmpty()) {
            return;
        }

        for (MapRecord<String, Object, Object> msg : messages) {
            if (msg.getStream() == null) {
                log.warn("Received a record without stream key: {}", msg);
                continue;
            }

            String streamKey = msg.getStream();
            lastIds.put(streamKey, msg.getId().getValue());
            handleMessage(streamKey, msg);
        }
    }

    private void handleMessage(String streamKey, MapRecord<String, Object, Object> msg) {
        switch (streamKey) {
            case WS_SESSION_TERMINATE_STREAM -> handleTerminate(msg);
            case USER_ACTIVITY_STREAM -> handleUserActivity(msg);
            default -> log.warn("Unhandled stream: {} message: {}", streamKey, msg);
        }
    }

    private void handleTerminate(MapRecord<String, Object, Object> msg) {
        String clientId = msg.getValue().get(FIELD_CLIENT_ID).toString();
        log.info("[RedisStream] stream=WS_SESSION_TERMINATE_STREAM clientId={}", clientId);
        webSocketSessionManager.removeAllSessions(clientId);
    }

    private void handleUserActivity(MapRecord<String, Object, Object> msg) {
        String clientId = msg.getValue().get(FIELD_CLIENT_ID).toString();
        log.info("[RedisStream] stream=USER_ACTIVITY_STREAM clientId={}", clientId);
        userAccessManager.recordAccess(clientId);
    }
}