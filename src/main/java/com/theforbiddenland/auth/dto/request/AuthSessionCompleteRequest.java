package com.theforbiddenland.auth.dto.request;

public record AuthSessionCompleteRequest(
        String sid,
        int version
) {
}
