package com.wishlist.like.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "likes")
@CompoundIndex(name = "user_item_idx", def = "{'userId': 1, 'itemId': 1}", unique = true)
@Builder
@Getter
public class Like {

    private String userId;
    private String itemId;

    public static Like of(String userId, String itemId) {
        return Like.builder()
                .userId(userId)
                .itemId(itemId)
                .build();
    }
}
