package com.theforbiddenland.chat.dto;

import com.theforbiddenland.chat.entity.ChatMessageEntity;
import com.theforbiddenland.chat.enums.ChatType;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ChatMessageDto (

        String id,
        String senderUid,
        String receiverUid,
        String content,
        Instant sendAt,
        ChatType type
) {

    public static ChatMessageDto of(ChatMessageEntity chatMessageEntity, String senderUid, String receiverUid) {
        return ChatMessageDto.builder()
                .id(chatMessageEntity.getId())
                .senderUid(senderUid)
                .receiverUid(receiverUid)
                .content(chatMessageEntity.getContent())
                .sendAt(chatMessageEntity.getSendAt())
                .type(chatMessageEntity.getType())
                .build();
    }
}
