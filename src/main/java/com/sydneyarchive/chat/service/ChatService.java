package com.sydneyarchive.chat.service;

import com.sydneyarchive.chat.dto.request.ChatMessageRequest;
import com.sydneyarchive.chat.dto.response.ChatMessageResponse;
import com.sydneyarchive.chat.dto.response.ChatRoomResponse;
import com.sydneyarchive.chat.entity.ChatMessage;
import com.sydneyarchive.chat.enums.ChatType;
import com.sydneyarchive.chat.repository.ChatMessageRepository;
import com.sydneyarchive.chat.repository.ChatMessageRepositoryImpl;
import com.sydneyarchive.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageRepositoryImpl chatMessageRepositoryImpl;
    private final UserService userService;

    public void processUserMessage(ChatMessageRequest chatMessageRequest) {
        ChatMessageResponse payload = saveChatMessage(chatMessageRequest);
        sendMessageToUser(payload);
        sendMessageToAdmin(payload);

        if (payload.type() == ChatType.USER) {
            userService.updateLastMessageAt(payload.senderSid(), payload.createdAt());
        }
    }

    public void processAdminMessage(ChatMessageRequest chatMessageRequest) {
        ChatMessageResponse payload = saveChatMessage(chatMessageRequest);
        sendMessageToAdmin(payload);
        sendMessageToUser(payload);
    }

    private ChatMessageResponse saveChatMessage(ChatMessageRequest chatMessageRequest) {
        ChatMessage savedChatMessage = chatMessageRepository.save(ChatMessage.of(chatMessageRequest));

        if (chatMessageRequest.type() == ChatType.USER) {
            return ChatMessageResponse.of(savedChatMessage);
        }
        return ChatMessageResponse.maskAdminSid(savedChatMessage);
    }

    private void sendMessageToAdmin(ChatMessageResponse payload) {
        messagingTemplate.convertAndSend("/topic/admin.chat", payload);
    }

    private void sendMessageToUser(ChatMessageResponse payload) {
        String user = (payload.type() == ChatType.USER)
                ? payload.senderSid() : payload.receiverSid();

        messagingTemplate.convertAndSendToUser(user, "/queue/chat.messages", payload);
    }

    public List<ChatRoomResponse> findAllChatRooms() {
        return userService.findAllUsersHavingLastMessage()
                .stream()
                .map(ChatRoomResponse::of)
                .toList();
    }

    public List<ChatMessageResponse> getChatMessages(String sid, String cursorId) {
        int limit = 15;
        return chatMessageRepositoryImpl
                .findByChatRoomIdAndBeforeCursor(sid, cursorId, limit)
                .stream()
                .map(ChatMessageResponse::of)
                .toList();
    }
}
