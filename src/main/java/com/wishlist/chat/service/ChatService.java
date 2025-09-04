package com.wishlist.chat.service;

import com.wishlist.chat.dto.ChatMessageDto;
import com.wishlist.chat.entity.ChatMessageEntity;
import com.wishlist.chat.enums.ChatType;
import com.wishlist.chat.repository.ChatMessageRepository;
import com.wishlist.chat.repository.ChatMessageRepositoryImpl;
import com.wishlist.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageRepositoryImpl chatMessageRepositoryImpl;
    private final UserService userService;

    public void processUserMessage(ChatMessageDto chatMessageDto) {
        log.info("📩 User[{}] → Admin[{}]: {}", chatMessageDto.sender(), chatMessageDto.receiver(), chatMessageDto.content());

        userService.saveGuest(chatMessageDto.sender());
        saveChatMessage(chatMessageDto);
        sendMessageToAdmin(chatMessageDto);
        sendAutoMessageToUser(chatMessageDto);
    }

    private void saveChatMessage(ChatMessageDto chatMessageDto) {
        chatMessageRepository.save(ChatMessageEntity.of(chatMessageDto));
    }

    private void sendMessageToAdmin(ChatMessageDto chatMessageDto) {
        messagingTemplate.convertAndSend("/topic/admin.chat", chatMessageDto);
    }

    private void sendAutoMessageToUser(ChatMessageDto chatMessageDto) {
        ChatMessageDto autoReply = ChatMessageDto.builder()
                .sender("wishlist-admin")
                .receiver(chatMessageDto.sender())
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

    public void processAdminMessage(ChatMessageDto chatMessageDto) {
        log.info("👩‍💻 Admin[{}] → User[{}]: {}", chatMessageDto.sender(), chatMessageDto.receiver(), chatMessageDto.content());
        saveChatMessage(chatMessageDto);
        messagingTemplate.convertAndSendToUser(
                chatMessageDto.receiver(),
                "/queue/chat.messages",
                chatMessageDto
        );
    }

    public List<ChatMessageDto> getChatMessages(String clientId, String cursorId, boolean isAdmin) {
        int limit = isAdmin ? 15 : 10;
        return chatMessageRepositoryImpl
                .findByClientIdBeforeId(clientId, cursorId, limit)
                .stream()
                .map(ChatMessageDto::of)
                .toList();
    }
}
