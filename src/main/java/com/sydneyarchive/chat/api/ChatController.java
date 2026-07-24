package com.sydneyarchive.chat.api;

import com.sydneyarchive.chat.dto.response.ChatMessageResponse;
import com.sydneyarchive.chat.dto.response.ChatRoomResponse;
import com.sydneyarchive.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/messages")
    public ResponseEntity<?> getChatMessages(
            @RequestParam String sid,
            @RequestParam(required = false) String cursorId
    ) {
        List<ChatMessageResponse> response = chatService.getChatMessages(sid, cursorId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rooms")
    public ResponseEntity<?> getRecentChatRooms() {
        List<ChatRoomResponse> response = chatService.findAllChatRooms();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/rooms/{chatRoomId}")
    public ResponseEntity<?> deleteChatRoom(@PathVariable String chatRoomId) {
        chatService.deleteChatRoom(chatRoomId);
        return ResponseEntity.ok().build();
    }
}
