package com.wishlist.global.exception.dto;

import lombok.Builder;

@Builder
public record ErrorResponse (
        String code,
        String message,
        int status
) { }
