package com.forbiddenland.auth.dto.request;

public record AccessTokenIssueRequest(
        String loginSessionSid
) {
}
