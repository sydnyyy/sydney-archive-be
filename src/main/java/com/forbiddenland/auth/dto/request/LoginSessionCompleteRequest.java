package com.forbiddenland.auth.dto.request;

public record LoginSessionCompleteRequest(
        String sid,
        int version
) {
}
