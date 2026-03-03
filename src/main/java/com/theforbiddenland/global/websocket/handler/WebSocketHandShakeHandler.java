package com.theforbiddenland.global.websocket.handler;

import com.theforbiddenland.global.websocket.dto.StompPrincipal;
import com.theforbiddenland.global.websocket.interceptor.WebSocketHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketHandShakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {

        String clientId = (String) attributes.get(WebSocketHandshakeInterceptor.CLIENT_ID_KEY);
        log.info("[determineUser] clientId={}", clientId);

        return new StompPrincipal(clientId != null ? clientId : UUID.randomUUID().toString());
    }
}
