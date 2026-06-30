package com.sydneyarchive.auth.dto.request;

public record AccessTokenIssueRequest(
        String loginSessionSid
) {
}
