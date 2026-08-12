package com.sydneyarchive.auth.dto.response;

import lombok.Builder;

@Builder
public record LoginSessionResponse(
        String qrCodeBase64,
        String sid,
        long expiredAt
) {
    public static LoginSessionResponse of(String qrCodeBase64, String sid, long expiredAt) {
        return LoginSessionResponse.builder()
                .qrCodeBase64(qrCodeBase64)
                .sid(sid)
                .expiredAt(expiredAt)
                .build();
    }
}