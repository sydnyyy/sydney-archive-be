package com.sydneyarchive.chat.api;

import com.sydneyarchive.auth.security.UserPrincipal;
import com.sydneyarchive.chat.dto.response.ChatMessageResponse;
import com.sydneyarchive.chat.dto.response.ChatRoomResponse;
import com.sydneyarchive.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/g/chat/messages")
    public ResponseEntity<?> getMyChatMessages(
            @RequestParam(required = false) String cursorId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        List<ChatMessageResponse> response = chatService.getChatMessages(userPrincipal.getUserId(), cursorId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/a/chat/messages")
    public ResponseEntity<?> getChatMessagesForAdmin(
            @RequestParam String userid,
            @RequestParam(required = false) String cursorId
    ) {
        List<ChatMessageResponse> response = chatService.getChatMessages(userid, cursorId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/a/chat/rooms")
    public ResponseEntity<?> getRecentChatRooms() {
        List<ChatRoomResponse> response = chatService.findAllChatRooms();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/a/chat/rooms/{chatRoomId}")
    public ResponseEntity<?> deleteChatRoom(@PathVariable String chatRoomId) {
        chatService.deleteChatRoom(chatRoomId);
        return ResponseEntity.ok().build();
    }
}
