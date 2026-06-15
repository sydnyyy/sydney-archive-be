package com.theforbiddenland.auth.dto.internal;

import lombok.Builder;

@Builder
public record AuthSessionContext(
        String qrCodeBase64,
        String sid,
        String authCode,
        long expiredAt
) {

    public static AuthSessionContext of(String qrCodeBase64, String sid, String authCode, long expiredAt) {
        return AuthSessionContext.builder()
                .qrCodeBase64(qrCodeBase64)
                .sid(sid)
                .authCode(authCode)
                .expiredAt(expiredAt)
                .build();
    }
}