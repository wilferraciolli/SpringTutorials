package com.wiltech.core;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

@Service
public class EventPublisher implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;

    public EventPublisher() {
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    public void publishEvent(DomainEvent event) {
        this.publisher.publishEvent(event);
    }
}
