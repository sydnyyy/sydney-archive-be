package com.wishlist.chat.entity;

import com.wishlist.chat.dto.ChatMessageDto;
import com.wishlist.chat.enums.ChatType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "chat_messages")
@Builder
@Getter
public class ChatMessageEntity {

    @Id
    private String id;

    private String userId;
    private String senderUserId;
    private String receiverUserId;
    private String content;
    private Instant sendAt;
    private ChatType type;

    public static ChatMessageEntity of(ChatMessageDto chatMessageDto, String senderUserId, String receiverUserId) {
        return ChatMessageEntity.builder()
                .userId(chatMessageDto.type().equals(ChatType.USER) ? senderUserId : receiverUserId)
                .senderUserId(senderUserId)
                .receiverUserId(receiverUserId)
                .content(chatMessageDto.content())
                .sendAt(chatMessageDto.sendAt())
                .type(chatMessageDto.type())
                .build();
    }
}
