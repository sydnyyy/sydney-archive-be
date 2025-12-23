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
import java.util.stream.Collectors;

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

    private final Map<String, Set<WebSocketSession>> sessionsByUser = new ConcurrentHashMap<>();

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

        sessionsByUser
                .computeIfAbsent(clientId, k -> ConcurrentHashMap.newKeySet())
                .add(session);
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
        Set<WebSocketSession> sessions = sessionsByUser.get(clientId);
        if (sessions == null || sessions.isEmpty()) return;

        Set<WebSocketSession> snapshot = Set.copyOf(sessions);
        snapshot.forEach(session -> {
            log.info("[removeAllSessions] WebSocket session 강제 종료. clientId={}, sessionId={}", clientId, session.getId());
            removeSession(session);
        });
    }

    public void removeSession(WebSocketSession session) {
        webSocketSessionCloser.closeSession(session, CloseStatus.NORMAL);

        String clientId = session.getAttributes().get(WebSocketHandshakeInterceptor.CLIENT_ID_KEY).toString();
        if (!sessionsByUser.containsKey(clientId)) {
            return;
        }

        String mainKey = WS_SESSION_MAIN_KEY_PREFIX + clientId;
        String terminateSignalKey = WS_TERMINATE_SIGNAL_KEY_PREFIX + clientId;

        redisTemplate.execute(
                removeSessionScript,
                List.of(mainKey, terminateSignalKey),
                session.getId()
        );

        Set<WebSocketSession> sessions = sessionsByUser.get(clientId);
        if (sessions == null) return;

        sessions.remove(session);
        if (sessions.isEmpty()) {
            sessionsByUser.remove(clientId);
        }
    }

    public Set<String> getReadOnlyLocalSessions(String clientId) {
        Set<WebSocketSession> sessions = sessionsByUser.get(clientId);
        if (sessions == null || sessions.isEmpty()) {
            return Collections.emptySet();
        }

        return sessions.stream()
                .map(WebSocketSession::getId)
                .collect(Collectors.toUnmodifiableSet());
    }

    public boolean hasSessionByClientId(String clientId) {
        Set<WebSocketSession> sessions = sessionsByUser.get(clientId);
        return sessions != null && !sessions.isEmpty();
    }

    public Collection<WebSocketSession> getAllSessions() {
        return sessionsByUser.values().stream()
                .flatMap(Set::stream)
                .collect(Collectors.toUnmodifiableList());
    }
}
