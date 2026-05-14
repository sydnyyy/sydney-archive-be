package com.theforbiddenland.chat.service;

import com.theforbiddenland.chat.dto.ChatMessageDto;
import com.theforbiddenland.chat.entity.ChatMessageEntity;
import com.theforbiddenland.chat.enums.ChatType;
import com.theforbiddenland.chat.repository.ChatMessageRepository;
import com.theforbiddenland.chat.repository.ChatMessageRepositoryImpl;
import com.theforbiddenland.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

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
        log.info("📩 UserSid[{}] → AdminSid[{}]: {}", chatMessageDto.senderSid(), chatMessageDto.receiverSid(), chatMessageDto.content());

        userService.saveGuest(chatMessageDto.senderSid());
        userService.updateLastMessageAt(chatMessageDto.senderSid(), chatMessageDto.sendAt());

        chatMessageDto = saveChatMessage(chatMessageDto);

        String user = chatMessageDto.type() == ChatType.SYSTEM
                ? chatMessageDto.receiverSid()
                : chatMessageDto.senderSid();

        sendMessageToUser(user, chatMessageDto);
        sendMessageToAdmin(chatMessageDto);
    }

    private ChatMessageDto saveChatMessage(ChatMessageDto chatMessageDto) {
        ChatMessageEntity chatMessage = ChatMessageEntity.of(chatMessageDto, chatMessageDto.senderSid(), chatMessageDto.receiverSid());
        ChatMessageEntity chatMessageEntity = chatMessageRepository.save(chatMessage);
        return ChatMessageDto.of(chatMessageEntity, chatMessageDto.senderSid(), chatMessageDto.receiverSid());
    }

    private void sendMessageToAdmin(ChatMessageDto chatMessageDto) {
        messagingTemplate.convertAndSend("/topic/admin.chat", chatMessageDto);
    }

    private void sendMessageToUser(String user, ChatMessageDto payload) {
        messagingTemplate.convertAndSendToUser(user, "/queue/chat.messages", payload);
    }

    public void processAdminMessage(ChatMessageDto chatMessageDto) {
        log.info("👩‍💻 AdminSid[{}] → UserSid[{}]: {}", chatMessageDto.senderSid(), chatMessageDto.receiverSid(), chatMessageDto.content());

        chatMessageDto = saveChatMessage(chatMessageDto);

        sendMessageToAdmin(chatMessageDto);
        sendMessageToUser(chatMessageDto.receiverSid(), chatMessageDto);
    }

    public List<ChatMessageDto> getChatMessages(String sid, String cursorId, boolean isAdmin) {
        int limit = isAdmin ? 15 : 10;
        return chatMessageRepositoryImpl
                .findBySidAndBeforeId(sid, cursorId, limit)
                .stream()
                .map(chatMessage -> {
                    return ChatMessageDto.of(chatMessage, chatMessage.getSenderSid(), chatMessage.getReceiverSid());
                })
                .toList();
    }
}
