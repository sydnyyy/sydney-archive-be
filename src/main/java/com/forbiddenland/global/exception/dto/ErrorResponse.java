package com.forbiddenland.global.exception.dto;

import lombok.Builder;

@Builder
public record ErrorResponse (
        String code,
        String message,
        int status
) { }
