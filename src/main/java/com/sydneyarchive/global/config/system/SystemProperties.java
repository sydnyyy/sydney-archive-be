package com.sydneyarchive.global.config.system;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.system")
public record SystemProperties(
        int bufferTimeSec
) {
}
