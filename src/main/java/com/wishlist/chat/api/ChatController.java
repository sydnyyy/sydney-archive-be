package com.wishlist.chat.api;

import com.wishlist.chat.dto.ChatMessage;
import com.wishlist.chat.enums.ChatType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    // 사용자 → 서버
    @MessageMapping("/chat.send")
    public void handleUserMessage(ChatMessage chatMessage) {
        log.info("📩 User[{}] → Admin[{}]: {}", chatMessage.sender(), chatMessage.receiver(), chatMessage.content());

        // 어드민 화면에 전달
        messagingTemplate.convertAndSend("/topic/admin.chat", chatMessage);

        if (chatMessage.type().equals(ChatType.USER)) {
            // TODO: 한 번 전송으로 변경
            ChatMessage autoReply = ChatMessage.builder()
                    .sender("wishlist-admin")
                    .receiver(chatMessage.sender())
                    .content("문의 감사합니다. 곧 답변드리겠습니다 🙏")
                    .sendAt(LocalDateTime.now(ZoneOffset.UTC))
                    .type(ChatType.SYSTEM)
                    .build();

            messagingTemplate.convertAndSendToUser(
                    autoReply.receiver(),
                    "/queue/chat.messages",
                    autoReply
            );
        }
    }

    // 어드민 → 사용자
    @MessageMapping("/chat.sendToUser")
    public void handleAdminMessage(ChatMessage chatMessage) {
        log.info("👩‍💻 Admin[{}] → User[{}]: {}", chatMessage.sender(), chatMessage.receiver(), chatMessage.content());

        ChatMessage adminResponse = ChatMessage.builder()
                .sender("wishlist-admin")
                .receiver(chatMessage.receiver())
                .content(chatMessage.content())
                .sendAt(LocalDateTime.now(ZoneOffset.UTC))
                .type(ChatType.ADMIN)
                .build();

        messagingTemplate.convertAndSendToUser(
                adminResponse.receiver(),
                "/queue/chat.messages",
                adminResponse
        );
    }
}
