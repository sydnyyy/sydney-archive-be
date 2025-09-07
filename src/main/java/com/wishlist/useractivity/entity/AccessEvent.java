package com.wishlist.useractivity.entity;

import java.time.Instant;

public record AccessEvent(
        String clientId,
        String cardId,
        Instant accessTime
) {
}
