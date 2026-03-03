package com.theforbiddenland.global.websocket.service;

import com.theforbiddenland.global.websocket.dto.SystemEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SystemEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendSessionTermination(String clientId, String sessionId, boolean shouldTerminate) {
        SystemEventDto event = SystemEventDto.builder()
                .type("SESSION_EXPIRED")
                .clientId(clientId)
                .sessionId(sessionId)
                .shouldTerminate(shouldTerminate)
                .build();

        messagingTemplate.convertAndSendToUser(
                clientId,
                "/queue/system",
                event
        );
    }
}
