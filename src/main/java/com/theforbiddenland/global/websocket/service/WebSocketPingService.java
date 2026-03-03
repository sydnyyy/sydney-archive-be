package com.theforbiddenland.global.websocket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Service
@Slf4j
public class WebSocketPingService {

    public void sendPing(WebSocketSession session) {
        if (session == null || !session.isOpen()) return;

        try {
            session.sendMessage(new PingMessage());
            log.info("[PING 발신 완료] sessionId={}", session.getId());
        } catch (IOException e) {
            log.info("[PING 발신 실패] sessionId={}", session.getId(), e);
        }
    }
}
