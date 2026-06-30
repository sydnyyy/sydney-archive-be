package com.sydneyarchive.global.websocket.controller;

import com.sydneyarchive.global.websocket.dto.SystemEventDto;
import com.sydneyarchive.global.websocket.service.SessionLifecycleService;
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
            sessionLifecycleService.processTerminate(response);
        } else {
            sessionLifecycleService.processMaintain(response);
        }
    }
}