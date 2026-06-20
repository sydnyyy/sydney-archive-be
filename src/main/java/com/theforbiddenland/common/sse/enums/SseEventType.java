package com.theforbiddenland.common.sse.enums;

import lombok.Getter;

@Getter
public enum SseEventType {

    CONNECTED("SSE connected"),
    ;

    private final String message;

    SseEventType(String message) {
        this.message = message;
    }
}
