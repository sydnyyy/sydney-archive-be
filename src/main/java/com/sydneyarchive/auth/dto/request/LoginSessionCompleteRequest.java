package com.sydneyarchive.auth.dto.request;

public record LoginSessionCompleteRequest(
        String sid,
        int version
) {
}
