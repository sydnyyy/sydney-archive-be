package com.wishlist.global.websocket.manager;

import com.wishlist.global.websocket.interceptor.WebSocketHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
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

    private final Map<String, Set<String>> clientSessionIds = new ConcurrentHashMap<>();  // { clientId, Set<sessionId> }
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();  // { sessionId, session }

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

        sessionMap.put(session.getId(), session);
        clientSessionIds.computeIfAbsent(clientId, k -> ConcurrentHashMap.newKeySet())
                .add(session.getId());
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
        Set<String> sessionIds = clientSessionIds.remove(clientId);
        if (sessionIds == null || sessionIds.isEmpty()) return;

        sessionIds.forEach(sessionId -> {
            WebSocketSession session = sessionMap.get(sessionId);
            log.info("[removeAllSessions] WebSocket session 강제 종료 시작. clientId={}, sessionId={}", clientId, sessionId);
            try {
                webSocketSessionCloser.closeSession(session, CloseStatus.NORMAL);
            } catch (IOException e) {
                log.info("[removeAllSessions] WebSocket session 강제 종료 실패. clientId={}, sessionId={}", clientId, sessionId);
                // TODO: 디스코드 경고 알림
            } finally {
                removeSession(session);
            }
        });
    }

    public void removeSession(WebSocketSession session) {
        String sessionId = session.getId();
        if (!sessionMap.containsKey(sessionId)) {
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

        sessionMap.remove(session.getId());
        Set<String> sessionIds = clientSessionIds.get(clientId);
        if (sessionIds != null) {
            sessionIds.remove(session.getId());
            if (sessionIds.isEmpty()) {
                clientSessionIds.remove(clientId, sessionIds);
            }
        }
    }

    public Set<String> getReadOnlyLocalSessions(String clientId) {
        return Collections.unmodifiableSet(clientSessionIds.get(clientId));
    }

    public boolean hasSession(String clientId) {
        return clientSessionIds.containsKey(clientId) &&
                !clientSessionIds.get(clientId).isEmpty();
    }

    public Collection<WebSocketSession> getAllSessions() {
        return Collections.unmodifiableCollection(sessionMap.values());
    }
}
