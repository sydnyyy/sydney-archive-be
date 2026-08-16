package com.sydneyarchive.auth.dto.internal;

import lombok.Builder;

@Builder
public record JwtContext(
        String accessToken,
        String refreshToken
) {

    public static JwtContext of(String accessToken, String refreshToken) {
        return JwtContext.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
