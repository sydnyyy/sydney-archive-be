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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

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
        userService.updateLastMessageAt(chatMessageDto.sender(), chatMessageDto.sendAt());

        chatMessageDto = saveChatMessage(chatMessageDto);

        sendMessageToUser(chatMessageDto.sender(), chatMessageDto);
        sendMessageToAdmin(chatMessageDto);
        sendAutoMessageToUser(chatMessageDto);
    }

    private ChatMessageDto saveChatMessage(ChatMessageDto chatMessageDto) {
        ChatMessageEntity chatMessageEntity = chatMessageRepository.save(ChatMessageEntity.of(chatMessageDto));
        return ChatMessageDto.of(chatMessageEntity);
    }

    private void sendMessageToAdmin(ChatMessageDto chatMessageDto) {
        messagingTemplate.convertAndSend("/topic/admin.chat", chatMessageDto);
    }

    private void sendMessageToUser(String user, ChatMessageDto payload) {
        messagingTemplate.convertAndSendToUser(user, "/queue/chat.messages", payload);
    }

    private void sendAutoMessageToUser(ChatMessageDto chatMessageDto) {
        ChatMessageDto autoReply = ChatMessageDto.builder()
                .id(String.valueOf(UUID.randomUUID()))
                .sender("wishlist-admin")
                .receiver(chatMessageDto.sender())
                .content("문의 감사합니다. 곧 답변드리겠습니다 🙏")
                .sendAt(Instant.now())
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

        chatMessageDto = saveChatMessage(chatMessageDto);

        sendMessageToAdmin(chatMessageDto);
        sendMessageToUser(chatMessageDto.receiver(), chatMessageDto);
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
