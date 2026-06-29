package com.forbiddenland.global.config.sse;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.sse")
public record SseProperties(
        int sseTimeoutSec,
        int sseHeartbeatIntervalSec,
        int sseHeartbeatInitialDelaySec
) {
}
