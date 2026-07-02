package com.sydneyarchive.global.websocket.handler;

import com.sydneyarchive.global.websocket.dto.StompPrincipal;
import com.sydneyarchive.global.websocket.interceptor.WebSocketHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketHandShakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(
            @NotNull ServerHttpRequest request,
            @NotNull WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {

        String sid = (String) attributes.get(WebSocketHandshakeInterceptor.WS_ATTRIBUTE_SID_KEY);
        String role = (String) attributes.get(WebSocketHandshakeInterceptor.WS_ATTRIBUTE_ROLE_KEY);
        log.info("[WS determineUser] sid={}, role={}", sid, role);

        return new StompPrincipal(sid);
    }
}
