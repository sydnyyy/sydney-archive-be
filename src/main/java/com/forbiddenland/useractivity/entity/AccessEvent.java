package com.forbiddenland.useractivity.entity;

import java.time.Instant;

public record AccessEvent(
        String sid,
        String itemId,
        Instant accessTime
) {
}
