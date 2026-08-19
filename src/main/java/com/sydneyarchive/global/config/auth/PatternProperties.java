package com.sydneyarchive.global.config.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pattern")
public record PatternProperties(
        int unit,
        int multiple,
        double switchThreshold
) {
}
