package com.theforbiddenland.global.config.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "admin")
public record AdminProperties(
        String username,
        String email
) {
}
