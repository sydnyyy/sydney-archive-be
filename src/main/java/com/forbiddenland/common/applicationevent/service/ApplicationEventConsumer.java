package com.forbiddenland.common.applicationevent.service;

import com.forbiddenland.auth.service.LoginSessionService;
import com.forbiddenland.common.applicationevent.dto.internal.LoginSessionTask;
import com.forbiddenland.common.applicationevent.enums.EventType;
import com.forbiddenland.common.sse.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationEventConsumer {

    private final LoginSessionService loginSessionService;
    private final SseService sseService;

    @Async
    @EventListener
    public void consumeEvent(LoginSessionTask task) {
        if (task.eventType() == EventType.LOGIN_SESSION_DELETE) {
            loginSessionService.deleteLoginSession(task.sid());
            sseService.terminateSseEmitter(task.sid());
        }
    }
}
