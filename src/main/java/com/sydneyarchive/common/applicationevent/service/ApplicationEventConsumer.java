package com.sydneyarchive.common.applicationevent.service;

import com.sydneyarchive.auth.service.LoginSessionService;
import com.sydneyarchive.common.applicationevent.dto.internal.LoginSessionTask;
import com.sydneyarchive.common.applicationevent.enums.EventType;
import com.sydneyarchive.common.sse.service.SseService;
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
