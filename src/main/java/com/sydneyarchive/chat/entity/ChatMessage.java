package com.sydneyarchive.chat.entity;

import com.sydneyarchive.chat.dto.request.ChatMessageRequest;
import com.sydneyarchive.chat.enums.ChatType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "chat_messages")
@Builder
@Getter
public class ChatMessage {

    @Id
    private String id;

    private String chatRoomId;  // GUEST UserId 기준
    private String senderUserId;
    private String receiverUserId;
    private String content;

    @CreatedDate
    private Instant createdAt;

    private ChatType type;

    public static ChatMessage of(ChatMessageRequest chatMessageRequest) {
        return ChatMessage.builder()
                .chatRoomId(chatMessageRequest.type().equals(ChatType.USER)
                        ? chatMessageRequest.senderUserId() : chatMessageRequest.receiverUserId()
                )
                .senderUserId(chatMessageRequest.senderUserId())
                .receiverUserId(chatMessageRequest.receiverUserId())
                .content(chatMessageRequest.content())
                .type(chatMessageRequest.type())
                .build();
    }
}
