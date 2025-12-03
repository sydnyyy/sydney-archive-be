package com.wishlist.readingsession.enums;

public enum ReadingSessionStatus {
    OPEN(1),
    ONGOING(0),
    CLOSE(2);

    private final int priority;

    ReadingSessionStatus(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
