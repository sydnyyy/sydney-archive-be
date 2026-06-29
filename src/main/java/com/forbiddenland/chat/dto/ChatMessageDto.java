package com.forbiddenland.chat.dto;

import com.forbiddenland.chat.entity.ChatMessageEntity;
import com.forbiddenland.chat.enums.ChatType;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ChatMessageDto (

        String id,
        String senderSid,
        String receiverSid,
        String content,
        Instant sendAt,
        ChatType type
) {

    public static ChatMessageDto of(ChatMessageEntity chatMessageEntity, String senderSid, String receiverSid) {
        return ChatMessageDto.builder()
                .id(chatMessageEntity.getId())
                .senderSid(senderSid)
                .receiverSid(receiverSid)
                .content(chatMessageEntity.getContent())
                .sendAt(chatMessageEntity.getSendAt())
                .type(chatMessageEntity.getType())
                .build();
    }
}
