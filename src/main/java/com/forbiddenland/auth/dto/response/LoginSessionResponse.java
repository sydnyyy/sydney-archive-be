package com.forbiddenland.auth.dto.response;

import com.forbiddenland.auth.dto.internal.LoginSessionContext;
import lombok.Builder;

@Builder
public record LoginSessionResponse(
        String qrCodeBase64,
        String sid,
        long expiredAt
) {
    public static LoginSessionResponse of(LoginSessionContext loginSessionContext) {
        return LoginSessionResponse.builder()
                .qrCodeBase64(loginSessionContext.qrCodeBase64())
                .sid(loginSessionContext.sid())
                .expiredAt(loginSessionContext.expiredAt())
                .build();
    }
}