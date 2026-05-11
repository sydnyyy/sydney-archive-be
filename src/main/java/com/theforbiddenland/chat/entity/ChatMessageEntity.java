package com.theforbiddenland.chat.entity;

import com.theforbiddenland.chat.dto.ChatMessageDto;
import com.theforbiddenland.chat.enums.ChatType;
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
    private String senderSid;
    private String receiverSid;
    private String content;
    private Instant sendAt;
    private ChatType type;

    public static ChatMessageEntity of(ChatMessageDto chatMessageDto, String senderSid, String receiverSid) {
        return ChatMessageEntity.builder()
                .userId(chatMessageDto.type().equals(ChatType.USER) ? senderSid : receiverSid)
                .senderSid(senderSid)
                .receiverSid(receiverSid)
                .content(chatMessageDto.content())
                .sendAt(chatMessageDto.sendAt())
                .type(chatMessageDto.type())
                .build();
    }
}
