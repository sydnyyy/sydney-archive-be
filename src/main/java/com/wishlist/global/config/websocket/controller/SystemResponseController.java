package com.wishlist.global.config.websocket.controller;

import com.wishlist.global.config.websocket.dto.SystemEventDto;
import com.wishlist.global.config.websocket.service.SessionLifecycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class SystemResponseController {

    private final SessionLifecycleService sessionLifecycleService;

    @MessageMapping("/system.response")
    public void handleSystemResponse(SystemEventDto response) {
        if (response.shouldTerminate()) {
            System.out.println("종료 요청 받음: " + response.clientId());
        } else {
            sessionLifecycleService.processMaintain(response);
        }
    }
}