package com.sydneyarchive.global.websocket.handler;

import com.sydneyarchive.global.websocket.dto.StompPrincipal;
import com.sydneyarchive.global.websocket.interceptor.WebSocketHandshakeInterceptor;
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

        String sid = (String) attributes.get(WebSocketHandshakeInterceptor.SID_KEY);
        log.info("[determineUser] sid={}", sid);

        return new StompPrincipal(sid != null ? sid : UUID.randomUUID().toString());
    }
}
