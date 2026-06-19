package com.theforbiddenland.auth.dto.internal;

import lombok.Builder;

@Builder
public record LoginSessionContext(
        String qrCodeBase64,
        String sid,
        String authCode,
        long expiredAt
) {

    public static LoginSessionContext of(String qrCodeBase64, String sid, String authCode, long expiredAt) {
        return LoginSessionContext.builder()
                .qrCodeBase64(qrCodeBase64)
                .sid(sid)
                .authCode(authCode)
                .expiredAt(expiredAt)
                .build();
    }
}