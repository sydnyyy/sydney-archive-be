package com.wishlist.useractivity.entity;

import java.time.Instant;

public record AccessEvent(
        String uid,
        String itemId,
        Instant accessTime
) {
}
