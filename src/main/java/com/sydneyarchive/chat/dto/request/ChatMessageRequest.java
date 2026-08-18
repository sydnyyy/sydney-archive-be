package com.sydneyarchive.chat.dto.request;

import com.sydneyarchive.chat.enums.ChatType;

public record ChatMessageRequest(
        String senderUserId,
        String receiverUserId,
        String content,
        ChatType type
) {
}
