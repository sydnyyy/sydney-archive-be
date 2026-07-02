package com.sydneyarchive.global.websocket.handler;

import com.sydneyarchive.global.websocket.dto.WebSocketSessionInfo;
import com.sydneyarchive.global.websocket.manager.WebSocketSessionManager;
import com.sydneyarchive.useractivity.manager.UserAccessManager;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.nio.channels.ClosedChannelException;

@Slf4j
public class WebSocketSessionHandler extends WebSocketHandlerDecorator {

    public static final String WS_LAST_HEARTBEAT_TIME = "lastHeartbeatTime";

    private final WebSocketSessionManager webSocketSessionManager;
    private final UserAccessManager userAccessManager;

    public WebSocketSessionHandler(WebSocketHandler delegate,
                                   WebSocketSessionManager webSocketSessionManager,
                                   UserAccessManager userAccessManager) {
        super(delegate);
        this.webSocketSessionManager = webSocketSessionManager;
        this.userAccessManager = userAccessManager;
    }

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
        WebSocketSessionInfo info = WebSocketSessionInfo.of(session);
        log.info("[WS afterConnectionEstablished] WebSocket 세션 연결 성공. {}", info);

        session.getAttributes().put(WS_LAST_HEARTBEAT_TIME, System.currentTimeMillis());
        webSocketSessionManager.addSession(session);
        userAccessManager.recordAccess(info.sid());
        super.afterConnectionEstablished(session);
    }

    @Override
    public void handleTransportError(@NotNull WebSocketSession session, @NotNull Throwable exception) throws Exception {
        WebSocketSessionInfo info = WebSocketSessionInfo.of(session);
        if (isClosedChannelException(exception)) {
            log.warn("[WS handleTransportError] 비정상적인 채널 닫힘 감지(ClosedChannelException). {}", info);
        } else {
            log.error("[WS handleTransportError] WebSocket 전송 오류 발생. {}", info, exception);
        }
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, CloseStatus closeStatus) throws Exception {
        WebSocketSessionInfo info = WebSocketSessionInfo.of(session);
        if (closeStatus.getCode() != CloseStatus.NORMAL.getCode()) {
            log.warn("[WS afterConnectionClosed] 비정상적인 WebSocket 연결 종료. {}, closeStatus={}", info, closeStatus);
        } else {
            log.info("[WS afterConnectionClosed] WebSocket 연결 정상 종료. {}", info);
        }

        webSocketSessionManager.removeSession(session);
        super.afterConnectionClosed(session, closeStatus);
    }

    @Override
    public void handleMessage(@NotNull WebSocketSession session, @NotNull WebSocketMessage<?> message) throws Exception {
        if (message instanceof PongMessage) {
            log.info("[PONG 수신 완료] sessionId={}", session.getId());
            session.getAttributes().put(WS_LAST_HEARTBEAT_TIME, System.currentTimeMillis());
            return;
        }

        super.handleMessage(session, message);
    }

    private boolean isClosedChannelException(Throwable exception) {
        if (exception == null) {
            return false;
        }
        if (exception instanceof ClosedChannelException) {
            return true;
        }
        return isClosedChannelException(exception.getCause());
    }
}
