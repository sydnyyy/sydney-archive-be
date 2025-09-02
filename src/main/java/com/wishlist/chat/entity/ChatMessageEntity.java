package com.wishlist.chat.entity;

import com.wishlist.chat.dto.ChatMessageDto;
import com.wishlist.chat.enums.ChatType;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chat_messages")
@Builder
public class ChatMessageEntity {

    @Id
    private String id;

    private String clientId;
    private String sender;
    private String receiver;
    private String content;
    private LocalDateTime sendAt;
    private ChatType type;

    public static ChatMessageEntity of(ChatMessageDto chatMessageDto) {
        return ChatMessageEntity.builder()
                .clientId(chatMessageDto.type().equals(ChatType.USER) ? chatMessageDto.sender() : chatMessageDto.receiver())
                .sender(chatMessageDto.sender())
                .receiver(chatMessageDto.receiver())
                .content(chatMessageDto.content())
                .sendAt(chatMessageDto.sendAt())
                .type(chatMessageDto.type())
                .build();
    }
}
