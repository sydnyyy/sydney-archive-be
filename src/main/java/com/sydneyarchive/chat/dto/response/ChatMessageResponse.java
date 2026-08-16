package com.sydneyarchive.chat.dto.response;

import com.sydneyarchive.chat.entity.ChatMessage;
import com.sydneyarchive.chat.enums.ChatType;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ChatMessageResponse(
        String id,
        String chatRoomId,
        String senderUserId,
        String receiverUserId,
        String content,
        Instant createdAt,
        ChatType type
) {

    public static ChatMessageResponse of(ChatMessage chatMessage) {
        return ChatMessageResponse.builder()
                .id(chatMessage.getId())
                .chatRoomId(chatMessage.getChatRoomId())
                .senderUserId(chatMessage.getSenderUserId())
                .receiverUserId(chatMessage.getReceiverUserId())
                .content(chatMessage.getContent())
                .createdAt(chatMessage.getCreatedAt())
                .type(chatMessage.getType())
                .build();
    }

    public static ChatMessageResponse maskAdminSid(ChatMessage chatMessage) {
        return ChatMessageResponse.builder()
                .id(chatMessage.getId())
                .chatRoomId(chatMessage.getChatRoomId())
                .senderUserId("admin")
                .receiverUserId(chatMessage.getReceiverUserId())
                .content(chatMessage.getContent())
                .createdAt(chatMessage.getCreatedAt())
                .type(chatMessage.getType())
                .build();
    }
}
