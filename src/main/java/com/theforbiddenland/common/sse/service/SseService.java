package com.theforbiddenland.common.sse.service;

import com.theforbiddenland.auth.service.LoginSessionService;
import com.theforbiddenland.common.sse.enums.SseEventType;
import com.theforbiddenland.global.exception.ErrorCode;
import com.theforbiddenland.global.exception.SseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class SseService {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final LoginSessionService loginSessionService;

    public SseEmitter connect(String sid) {
        Long version = loginSessionService.getLoginSessionVersion(sid);
        if (version == -1) {
            throw new SseException(ErrorCode.LOGIN_SESSION_EXPIRED);
        }

        SseEmitter emitter = new SseEmitter();
        emitters.put(sid, emitter);

        emitter.onCompletion(() -> {
            log.info("[SSE] Connection Completed - SID: {}", sid);
            emitters.remove(sid);
        });

        emitter.onTimeout(() -> {
            log.warn("[SSE] Connection Timeout - SID: {}", sid);
            emitter.complete();
        });

        emitter.onError(e -> {
            log.error("[SSE] Connection Error - SID: {}, Message: {}", sid, e.getMessage());
            emitters.remove(sid);
        });

        try {
            emitter.send(SseEmitter.event()
                    .name(SseEventType.CONNECTED.name())
                    .data(Map.of(
                            "message", SseEventType.CONNECTED.getMessage(),
                            "version", version
                    ))
            );
        } catch (IOException e) {
            log.warn("SSE connection closed while sending event. sid={}", sid);
            emitter.complete();
        }

        return emitter;
    }

    public void sendEvent(String sid, SseEventType eventType, Object data) {
        SseEmitter emitter = emitters.get(sid);
        if (emitter == null) {
            log.warn("[SSE] Failed to send event '{}': No active emitter found for SID: {}",
                    eventType, sid);
            return;
        }

        try {
            emitter.send(SseEmitter.event()
                    .name(eventType.name())
                    .data(data)
            );
        } catch (IOException e) {
            log.warn("SSE connection closed while sending event. sid={}", sid);
            emitter.complete();
        }
    }

}
