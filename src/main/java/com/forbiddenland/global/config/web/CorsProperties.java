package com.forbiddenland.global.config.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.cors")
public record CorsProperties(
        String[] allowedOrigins
) { }