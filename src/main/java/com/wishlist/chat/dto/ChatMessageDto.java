package com.wishlist.chat.dto;

import com.wishlist.chat.enums.ChatType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatMessageDto (

        String sender,
        String receiver,
        String content,
        LocalDateTime sendAt,
        ChatType type
) {
}
