package com.theforbiddenland.auth.dto.response;

import com.theforbiddenland.auth.dto.internal.AuthSessionContext;
import lombok.Builder;

@Builder
public record AuthSessionResponse(
        String qrCodeBase64,
        String sid,
        long expiredAt
) {
    public static AuthSessionResponse of(AuthSessionContext authSessionContext) {
        return AuthSessionResponse.builder()
                .qrCodeBase64(authSessionContext.qrCodeBase64())
                .sid(authSessionContext.sid())
                .expiredAt(authSessionContext.expiredAt())
                .build();
    }
}