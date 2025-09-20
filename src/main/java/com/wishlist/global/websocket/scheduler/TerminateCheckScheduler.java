package com.wishlist.global.websocket.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static com.wishlist.global.websocket.constant.WebSocketKeys.*;

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
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(
                LOCK_KEY,
                serverId,
                Duration.ofMillis(LOCK_EXPIRE_MILLIS)
        );

        if (Boolean.TRUE.equals(locked)) {
            log.info("[checkTerminateCandidates] terminate_scheduler 분산락 획득");

            try {
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
            } finally {
                if (serverId.equals(redisTemplate.opsForValue().get(LOCK_KEY))) {
                    redisTemplate.delete(LOCK_KEY);
                    log.info("[checkTerminateCandidates] terminate_scheduler 분산락 해제");
                } else {
                    log.warn("[checkTerminateCandidates] terminate_scheduler 분산락 해제 실패 (다른 서버가 획득)");
                }
            }
        } else {
            log.debug("[checkTerminateCandidates] 다른 서버에서 terminate_scheduler 분산락 획득");
        }
    }
}

