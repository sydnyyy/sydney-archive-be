package com.sydneyarchive.chat.api;

import com.sydneyarchive.chat.dto.response.ChatRoomResponse;
import com.sydneyarchive.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/chat")
@RequiredArgsConstructor
public class AdminChatController {

    private final ChatService chatService;

    @GetMapping("/rooms")
    public ResponseEntity<?> getRecentChatRooms() {
        List<ChatRoomResponse> response = chatService.findAllChatRooms();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{sid}")
    public ResponseEntity<?> deleteChatRoom(@PathVariable String sid) {
        chatService.deleteChatRoom(sid);
        return ResponseEntity.ok().build();
    }
}
