package com.wishlist.global.websocket.interceptor;

import com.wishlist.global.websocket.manager.WebSocketSessionManager;
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

    public static final String CLIENT_ID_KEY = "client_id";
    public static final String TAB_ID_KEY = "tab_id";
    private final WebSocketSessionManager webSocketSessionManager;

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request,
                                   @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler,
                                   @NonNull Map<String, Object> attributes) throws Exception {

        String clientId = getClientId(request);
        if (clientId == null || clientId.isBlank()) {
            log.warn("[beforeHandshake] Missing or empty Client ID in handshake request. URI: {}", request.getURI());
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }

        String tabId = getTabId(request);
        if (webSocketSessionManager.hasSessionByTabId(tabId)) {
            log.warn("[beforeHandshake] Duplicate WebSocket connection prevented. URI={}", request.getURI());
            return false;
        }

        attributes.put(CLIENT_ID_KEY, clientId);
        attributes.put(TAB_ID_KEY, tabId);
        return true;
    }

    private String getClientId(ServerHttpRequest request) {
        return UriComponentsBuilder
                .fromUri(request.getURI())
                .build()
                .getQueryParams()
                .getFirst(CLIENT_ID_KEY);
    }

    private String getTabId(ServerHttpRequest request) {
        return UriComponentsBuilder
                .fromUri(request.getURI())
                .build()
                .getQueryParams()
                .getFirst(TAB_ID_KEY);
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
