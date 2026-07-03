package com.sydneyarchive.chat.dto.request;

import com.sydneyarchive.chat.enums.ChatType;

public record ChatMessageRequest(

        String senderSid,
        String receiverSid,
        String content,
        ChatType type
) {
}
