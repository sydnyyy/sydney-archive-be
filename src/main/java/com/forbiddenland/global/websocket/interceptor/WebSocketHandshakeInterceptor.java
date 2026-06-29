package com.forbiddenland.global.websocket.interceptor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    public static final String SID_KEY = "sid";

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request,
                                   @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler,
                                   @NonNull Map<String, Object> attributes) throws Exception {

        String sid = getSid(request);
        if (sid == null || sid.isBlank()) {
            log.warn("[beforeHandshake] Missing or empty sid in handshake request. URI: {}", request.getURI());
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }

        attributes.put(SID_KEY, sid);
        return true;
    }

    private String getSid(ServerHttpRequest request) {
        return UriComponentsBuilder
                .fromUri(request.getURI())
                .build()
                .getQueryParams()
                .getFirst(SID_KEY);
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request,
                               @NonNull ServerHttpResponse response,
                               @NonNull WebSocketHandler wsHandler,
                               Exception exception) {

        if (exception != null) {
            log.warn("🔴 [afterHandshake] WebSocket handshake failed. Address={}", request.getRemoteAddress(), exception);
        } else {
            log.info("🟢 [afterHandshake] WebSocket handshake succeeded. Address={}", request.getRemoteAddress());
        }
    }
}
