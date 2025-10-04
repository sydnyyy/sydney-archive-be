package com.wishlist.global.websocket.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static com.wishlist.global.redis.constant.RedisKeys.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class TerminateCheckScheduler {

    private static final String LOCK_KEY = "distributed_lock:terminate_scheduler";
    private final String serverId = UUID.randomUUID().toString();
    private static final long LOCK_EXPIRE_MILLIS = 60_000L;

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<List> terminateCheckScript;

    private static final long SAFE_MILLIS = 60_000L;

    @Scheduled(fixedDelay = 30_000)
    public void checkTerminateCandidates() {
        List<String> processed = redisTemplate.execute(
                terminateCheckScript,
                List.of(WS_SESSION_TERMINATE_CHECK_ZSET,
                        WS_SESSION_TERMINATE_STREAM,
                        LOCK_KEY),
                String.valueOf(System.currentTimeMillis()),
                String.valueOf(SAFE_MILLIS),
                serverId,
                String.valueOf(LOCK_EXPIRE_MILLIS)
        );

        if (processed != null && !processed.isEmpty()) {
            log.info("[checkTerminateCandidates] processed clientIds: {}", processed);
        }
    }
}

