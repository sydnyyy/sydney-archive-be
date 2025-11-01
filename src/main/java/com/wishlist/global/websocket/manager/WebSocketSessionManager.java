package com.wishlist.global.websocket.manager;

import com.wishlist.global.websocket.interceptor.WebSocketHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.wishlist.global.redis.constant.RedisKeys.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketSessionManager {

    public static final Duration MAIN_KEY_TTL = Duration.ofHours(1);
    public static final Duration TERMINATE_SIGNAL_KEY_TTL = Duration.ofMinutes(25);
    public static final String TERMINATE_SIGNAL_KEY_DUMMY_VALUE = "1";

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> addSessionScript;
    private final DefaultRedisScript<Long> updateTTLScript;
    private final DefaultRedisScript<Long> removeSessionScript;
    private final WebSocketSessionCloser webSocketSessionCloser;

    private final Map<String, Set<String>> clientTabIds = new ConcurrentHashMap<>();  // { clientId, Set<tabId> }
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();  // { tabId, session }

    public void addSession(WebSocketSession session) {
        String clientId = session.getAttributes().get(WebSocketHandshakeInterceptor.CLIENT_ID_KEY).toString();
        String mainKey = WS_SESSION_MAIN_KEY_PREFIX + clientId;
        String terminateSignalKey = WS_TERMINATE_SIGNAL_KEY_PREFIX + clientId;

        redisTemplate.execute(
                addSessionScript,
                List.of(mainKey, terminateSignalKey),
                session.getId(),
                String.valueOf(MAIN_KEY_TTL.toSeconds()),
                TERMINATE_SIGNAL_KEY_DUMMY_VALUE,
                String.valueOf(TERMINATE_SIGNAL_KEY_TTL.toSeconds())
        );

        String tabId = session.getAttributes().get(WebSocketHandshakeInterceptor.TAB_ID_KEY).toString();
        sessionMap.put(tabId, session);
        clientTabIds
                .computeIfAbsent(clientId, k -> ConcurrentHashMap.newKeySet())
                .add(tabId);
    }

    public void updateTTL(String clientId) {
        String mainKey = WS_SESSION_MAIN_KEY_PREFIX + clientId;
        String terminateSignalKey = WS_TERMINATE_SIGNAL_KEY_PREFIX + clientId;

        redisTemplate.execute(
                updateTTLScript,
                List.of(mainKey, terminateSignalKey),
                String.valueOf(MAIN_KEY_TTL.toSeconds()),
                String.valueOf(TERMINATE_SIGNAL_KEY_TTL.toSeconds())
        );
    }

    public void removeAllSessions(String clientId) {
        Set<String> tabIds = clientTabIds.remove(clientId);
        if (tabIds == null || tabIds.isEmpty()) return;

        tabIds.forEach(tabId -> {
            WebSocketSession session = sessionMap.get(tabId);
            log.info("[removeAllSessions] WebSocket session 강제 종료 시작. clientId={}, sessionId={}", clientId, session.getId());
            webSocketSessionCloser.closeSession(session, CloseStatus.NORMAL);
            removeSession(session);
        });
    }

    public void removeSession(WebSocketSession session) {
        String tabId = session.getAttributes().get(WebSocketHandshakeInterceptor.TAB_ID_KEY).toString();
        if (!sessionMap.containsKey(tabId)) {
            return;
        }

        String clientId = session.getAttributes().get(WebSocketHandshakeInterceptor.CLIENT_ID_KEY).toString();
        String mainKey = WS_SESSION_MAIN_KEY_PREFIX + clientId;
        String terminateSignalKey = WS_TERMINATE_SIGNAL_KEY_PREFIX + clientId;

        redisTemplate.execute(
                removeSessionScript,
                List.of(mainKey, terminateSignalKey),
                session.getId()
        );

        sessionMap.remove(tabId);
        Set<String> tabIds = clientTabIds.get(clientId);
        if (tabIds != null) {
            tabIds.remove(tabId);
            if (tabIds.isEmpty()) {
                clientTabIds.remove(clientId, tabIds);
            }
        }
    }

    public Set<String> getReadOnlyLocalSessions(String clientId) {
        return Collections.unmodifiableSet(clientTabIds.get(clientId));
    }

    public boolean hasSessionByClientId(String clientId) {
        return clientTabIds.containsKey(clientId) &&
                !clientTabIds.get(clientId).isEmpty();
    }

    public boolean hasSessionByTabId(String tabId) {
        return sessionMap.containsKey(tabId);
    }

    public Collection<WebSocketSession> getAllSessions() {
        return Collections.unmodifiableCollection(sessionMap.values());
    }
}
