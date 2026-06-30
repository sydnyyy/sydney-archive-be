package com.sydneyarchive.chat.api;

import com.sydneyarchive.chat.dto.ChatMessageDto;
import com.sydneyarchive.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;

    // 사용자 → 서버
    @MessageMapping("/chat.send")
    public void handleUserMessage(ChatMessageDto chatMessageDto) {
        chatService.processUserMessage(chatMessageDto);
    }

    // 어드민 → 사용자
    @MessageMapping("/chat.sendToUser")
    public void handleAdminMessage(ChatMessageDto chatMessageDto) {
        chatService.processAdminMessage(chatMessageDto);
    }
}
