package com.wishlist.global.websocket.handler;

import com.wishlist.global.websocket.dto.WebSocketSessionInfo;
import com.wishlist.global.websocket.interceptor.WebSocketHandshakeInterceptor;
import com.wishlist.global.websocket.manager.WebSocketSessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.nio.channels.ClosedChannelException;

@Slf4j
public class WebSocketSessionHandler extends WebSocketHandlerDecorator {

    private final WebSocketSessionManager webSocketSessionManager;

    public WebSocketSessionHandler(WebSocketHandler delegate,
                                   WebSocketSessionManager webSocketSessionManager) {
        super(delegate);
        this.webSocketSessionManager = webSocketSessionManager;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String clientId = session.getAttributes().get(WebSocketHandshakeInterceptor.CLIENT_ID_KEY).toString();
        log.info("🟢 [afterConnectionEstablished] WebSocket 세션 연결 성공. clientId={}, sessionId={}", clientId, session.getId());

        webSocketSessionManager.addSession(session);
        super.afterConnectionEstablished(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session,
                                     Throwable exception) throws Exception {
        WebSocketSessionInfo info = WebSocketSessionInfo.of(session);
        if (isClosedChannelException(exception)) {
            log.warn("🔴 [handleTransportError] 비정상적인 채널 닫힘 감지(ClosedChannelException). {}", info);
        } else {
            log.error("🔴 [handleTransportError] WebSocket 전송 오류 발생. {}", info, exception);
        }
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session,
                                      CloseStatus closeStatus) throws Exception {
        WebSocketSessionInfo info = WebSocketSessionInfo.of(session);
        if (closeStatus.getCode() != CloseStatus.NORMAL.getCode()) {
            log.warn("🔴 [afterConnectionClosed] 비정상적인 WebSocket 연결 종료. {}, closeStatus={}", info, closeStatus);
        } else {
            log.info("🟢 [afterConnectionClosed] WebSocket 연결 정상 종료. {}", info);
        }

        webSocketSessionManager.removeSession(session);
        super.afterConnectionClosed(session, closeStatus);
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
