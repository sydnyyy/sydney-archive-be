package com.wishlist.global.config.redis;

import com.wishlist.global.config.websocket.manager.WebSocketSessionManager;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisStreamConsumer {

    public static final String STREAM_TERMINATE_SESSION = "terminate_session_stream";
    public static final String STREAM_OTHER_EVENT = "other_event_stream";
    public static final String FIELD_CLIENT_ID = "clientId";

    private final WebSocketSessionManager webSocketSessionManager;
    private final StringRedisTemplate redisTemplate;
    private static final Map<String, String> lastIds = new ConcurrentHashMap<>();

    static {
        lastIds.put(STREAM_TERMINATE_SESSION, "0");
        lastIds.put(STREAM_OTHER_EVENT, "0");
    }

    @Scheduled(fixedDelay = 1000)
    public void pollStreams() {
        List<MapRecord<String, Object, Object>> messages = redisTemplate.opsForStream()
                .read(StreamReadOptions.empty().count(100),
                        StreamOffset.create(STREAM_TERMINATE_SESSION, ReadOffset.from(lastIds.get(STREAM_TERMINATE_SESSION))));

        for (MapRecord<String, Object, Object> msg : messages) {
            String clientId = msg.getValue().get(FIELD_CLIENT_ID).toString();
            log.info("[WebSocket TerminateStream] Received terminate for clientId: {}", clientId);
            webSocketSessionManager.removeAllSessions(clientId);

            lastIds.put(STREAM_TERMINATE_SESSION, msg.getId().getValue());
        }
    }
}