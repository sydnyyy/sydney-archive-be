package com.forbiddenland.readingsession.enums;

import lombok.Getter;

@Getter
public enum ReadingSessionStatus {
    OPEN(1),
    ONGOING(0),
    CLOSE(2);

    private final int priority;

    ReadingSessionStatus(int priority) {
        this.priority = priority;
    }
}
