package com.sydneyarchive.common.applicationevent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationEventProducer {

    private final ApplicationEventPublisher publisher;

    public void publishEvent(Object event) {
        publisher.publishEvent(event);
    }
}
