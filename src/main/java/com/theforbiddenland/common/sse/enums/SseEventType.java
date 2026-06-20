package com.theforbiddenland.common.sse.enums;

import lombok.Getter;

@Getter
public enum SseEventType {

    CONNECTED("SSE connected"),
    LOGIN_SUCCEEDED("Login succeeded"),
    ;

    private final String message;

    SseEventType(String message) {
        this.message = message;
    }
}
