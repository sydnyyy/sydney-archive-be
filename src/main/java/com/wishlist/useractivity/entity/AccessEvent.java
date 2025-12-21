package com.wishlist.useractivity.entity;

import java.time.Instant;

public record AccessEvent(
        String uid,
        String cardId,
        Instant accessTime
) {
}
