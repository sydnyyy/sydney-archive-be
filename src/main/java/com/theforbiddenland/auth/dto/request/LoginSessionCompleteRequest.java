package com.theforbiddenland.auth.dto.request;

public record LoginSessionCompleteRequest(
        String sid,
        int version
) {
}
