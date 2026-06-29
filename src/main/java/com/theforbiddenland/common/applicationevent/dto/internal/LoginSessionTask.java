package com.theforbiddenland.common.applicationevent.dto.internal;

import com.theforbiddenland.common.applicationevent.enums.EventType;
import lombok.Builder;

@Builder
public record LoginSessionTask(
        EventType eventType,
        String sid
) {

    public static LoginSessionTask of(EventType eventType, String sid) {
        return LoginSessionTask.builder()
                .eventType(eventType)
                .sid(sid)
                .build();
    }
}
