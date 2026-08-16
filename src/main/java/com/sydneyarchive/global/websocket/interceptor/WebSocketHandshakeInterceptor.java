package com.sydneyarchive.global.websocket.interceptor;

import com.sydneyarchive.user.dto.internal.UserAuthContext;
import com.sydneyarchive.user.service.UserService;
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

    public static final String WS_ATTRIBUTE_SID_KEY = "sid";
    public static final String WS_ATTRIBUTE_ROLE_KEY = "role";
    private final UserService userService;

    @Override
    public boolean beforeHandshake(
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler,
            @NonNull Map<String, Object> attributes
    ) throws Exception {

        // 사용자에게 할당된 sid
        String sid = getSid(request);

        if (sid == null || sid.isBlank()) {
            log.warn("[WS beforeHandshake] Handshake rejected. Missing or empty sid in handshake request. URI={}", request.getURI());
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }

        try {
            UserAuthContext userAuthContext = userService.getUserContext(sid);
            attributes.put(WS_ATTRIBUTE_SID_KEY, sid);
            attributes.put(WS_ATTRIBUTE_ROLE_KEY, userAuthContext.role().toString());
            return true;
        } catch (Exception e) {
            log.warn("[WS beforeHandshake] Handshake rejected. Unknown sid: {}", sid);
            return false;
        }
    }

    private String getSid(ServerHttpRequest request) {
        return UriComponentsBuilder
                .fromUri(request.getURI())
                .build()
                .getQueryParams()
                .getFirst(WS_ATTRIBUTE_SID_KEY);
    }

    @Override
    public void afterHandshake(
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler,
            Exception exception
    ) {

        if (exception != null) {
            log.warn("[WS afterHandshake] WebSocket handshake failed. Address={}", request.getRemoteAddress(), exception);
        } else {
            log.info("[WS afterHandshake] WebSocket handshake succeeded. Address={}", request.getRemoteAddress());
        }
    }
}
