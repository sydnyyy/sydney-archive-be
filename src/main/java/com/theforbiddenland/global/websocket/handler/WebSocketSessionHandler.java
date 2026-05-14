package com.theforbiddenland.global.websocket.handler;

import com.theforbiddenland.global.websocket.dto.WebSocketSessionInfo;
import com.theforbiddenland.global.websocket.interceptor.WebSocketHandshakeInterceptor;
import com.theforbiddenland.global.websocket.manager.WebSocketSessionManager;
import com.theforbiddenland.useractivity.manager.UserAccessManager;
import lombok.extern.slf4j.Slf4j;
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
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sid = session.getAttributes().get(WebSocketHandshakeInterceptor.SID_KEY).toString();
        log.info("🟢 [afterConnectionEstablished] WebSocket 세션 연결 성공. SId={}, sessionId={}", sid, session.getId());

        session.getAttributes().put(WS_LAST_HEARTBEAT_TIME, System.currentTimeMillis());
        webSocketSessionManager.addSession(session);
        userAccessManager.recordAccess(sid);
        super.afterConnectionEstablished(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        WebSocketSessionInfo info = WebSocketSessionInfo.of(session);
        if (isClosedChannelException(exception)) {
            log.warn("🔴 [handleTransportError] 비정상적인 채널 닫힘 감지(ClosedChannelException). {}", info);
        } else {
            log.error("🔴 [handleTransportError] WebSocket 전송 오류 발생. {}", info, exception);
        }
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        WebSocketSessionInfo info = WebSocketSessionInfo.of(session);
        if (closeStatus.getCode() != CloseStatus.NORMAL.getCode()) {
            log.warn("🔴 [afterConnectionClosed] 비정상적인 WebSocket 연결 종료. {}, closeStatus={}", info, closeStatus);
        } else {
            log.info("🟢 [afterConnectionClosed] WebSocket 연결 정상 종료. {}", info);
        }

        webSocketSessionManager.removeSession(session);
        super.afterConnectionClosed(session, closeStatus);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message instanceof PongMessage pongMessage) {
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
