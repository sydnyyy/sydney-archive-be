package com.wishlist.global.websocket.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.wishlist.global.websocket.constant.WebSocketKeys.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class TerminateCheckScheduler {

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<List> terminateCheckScript;

    private static final long SAFE_MILLIS = 60_000L;

    @Scheduled(fixedDelay = 30_000)
    public void checkTerminateCandidates() {
        List<String> processed = redisTemplate.execute(
                terminateCheckScript,
                List.of(WEBSOCKET_SESSION_TERMINATE_CHECK_ZSET,
                        WEBSOCKET_SESSION_TERMINATE_STREAM),
                String.valueOf(System.currentTimeMillis()),
                String.valueOf(SAFE_MILLIS)
        );

        if (processed != null && !processed.isEmpty()) {
            log.info("[checkTerminateCandidates] processed clientIds: {}", processed);
        }
    }
}

