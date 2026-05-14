package com.theforbiddenland.chat.api;

import com.theforbiddenland.chat.dto.ChatMessageDto;
import com.theforbiddenland.chat.service.ChatService;
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
public class ChatRestController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<List<ChatMessageDto>> getChatMessages(
            @RequestParam String sid,
            @RequestParam(required = false) String cursorId,
            @RequestParam(defaultValue = "false") boolean isAdmin) {

        List<ChatMessageDto> chatMessageDtos = chatService.getChatMessages(sid, cursorId, isAdmin);
        return ResponseEntity.ok(chatMessageDtos);
    }
}
