package com.wishlist.chat.dto;

import com.wishlist.chat.entity.ChatMessageEntity;
import com.wishlist.chat.enums.ChatType;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ChatMessageDto (

        String id,
        String sender,
        String receiver,
        String content,
        Instant sendAt,
        ChatType type
) {

    public static ChatMessageDto of(ChatMessageEntity chatMessageEntity) {
        return ChatMessageDto.builder()
                .id(chatMessageEntity.getId())
                .sender(chatMessageEntity.getSender())
                .receiver(chatMessageEntity.getReceiver())
                .content(chatMessageEntity.getContent())
                .sendAt(chatMessageEntity.getSendAt())
                .type(chatMessageEntity.getType())
                .build();
    }
}
