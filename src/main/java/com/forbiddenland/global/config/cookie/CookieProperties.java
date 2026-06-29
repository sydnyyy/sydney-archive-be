package com.forbiddenland.global.config.cookie;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.cookie")
public record CookieProperties(
        boolean secure,
        String sameSite,
        String path
) {
}
