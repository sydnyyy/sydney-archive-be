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
        log.info("📩 UserUid[{}] → AdminUid[{}]: {}", chatMessageDto.senderUid(), chatMessageDto.receiverUid(), chatMessageDto.content());

        userService.saveGuest(chatMessageDto.senderUid());
        userService.updateLastMessageAt(chatMessageDto.senderUid(), chatMessageDto.sendAt());

        chatMessageDto = saveChatMessage(chatMessageDto);

        String user = chatMessageDto.type() == ChatType.SYSTEM
                ? chatMessageDto.receiverUid()
                : chatMessageDto.senderUid();

        sendMessageToUser(user, chatMessageDto);
        sendMessageToAdmin(chatMessageDto);
    }

    private ChatMessageDto saveChatMessage(ChatMessageDto chatMessageDto) {
        String senderUserId = userService.findUserIdByUid(chatMessageDto.senderUid());
        String receiverUserId = userService.findUserIdByUid(chatMessageDto.receiverUid());

        ChatMessageEntity chatMessage = ChatMessageEntity.of(chatMessageDto, senderUserId, receiverUserId);
        ChatMessageEntity chatMessageEntity = chatMessageRepository.save(chatMessage);
        return ChatMessageDto.of(chatMessageEntity, chatMessageDto.senderUid(), chatMessageDto.receiverUid());
    }

    private void sendMessageToAdmin(ChatMessageDto chatMessageDto) {
        messagingTemplate.convertAndSend("/topic/admin.chat", chatMessageDto);
    }

    private void sendMessageToUser(String user, ChatMessageDto payload) {
        messagingTemplate.convertAndSendToUser(user, "/queue/chat.messages", payload);
    }

    public void processAdminMessage(ChatMessageDto chatMessageDto) {
        log.info("👩‍💻 AdminUid[{}] → UserUid[{}]: {}", chatMessageDto.senderUid(), chatMessageDto.receiverUid(), chatMessageDto.content());

        chatMessageDto = saveChatMessage(chatMessageDto);

        sendMessageToAdmin(chatMessageDto);
        sendMessageToUser(chatMessageDto.receiverUid(), chatMessageDto);
    }

    public List<ChatMessageDto> getChatMessages(String userId, String cursorId, boolean isAdmin) {
        int limit = isAdmin ? 15 : 10;
        return chatMessageRepositoryImpl
                .findByUserIdAndBeforeId(userId, cursorId, limit)
                .stream()
                .map(chatMessage -> {
                    String senderUid = userService.findUidByUserId(chatMessage.getSenderUserId());
                    String receiverUid = userService.findUidByUserId(chatMessage.getReceiverUserId());
                    return ChatMessageDto.of(chatMessage, senderUid, receiverUid);
                })
                .toList();
    }
}
