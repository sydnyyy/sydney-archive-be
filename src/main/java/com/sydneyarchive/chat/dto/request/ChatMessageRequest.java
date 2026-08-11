package com.sydneyarchive.chat.dto.request;

import com.sydneyarchive.chat.enums.ChatType;

public record ChatMessageRequest(
        String senderUid,
        String receiverUid,
        String content,
        ChatType type
) {
}
