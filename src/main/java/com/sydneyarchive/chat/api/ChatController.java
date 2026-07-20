package com.sydneyarchive.chat.api;

import com.sydneyarchive.chat.dto.response.ChatMessageResponse;
import com.sydneyarchive.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat/messages")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<?> getChatMessages(
            @RequestParam String sid,
            @RequestParam(required = false) String cursorId
    ) {
        List<ChatMessageResponse> response = chatService.getChatMessages(sid, cursorId);
        return ResponseEntity.ok(response);
    }
}
