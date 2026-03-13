package com.batrobot.shared.infrastructure.event;

import com.batrobot.shared.application.event.DomainEventPublisher;
import com.batrobot.shared.domain.event.DomainEvent;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;

/**
 * Infrastructure implementation of DomainEventPublisher using Spring's ApplicationEventPublisher.
 */
@RequiredArgsConstructor
public class DomainEventPublisherImpl implements DomainEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(DomainEvent event) {
        eventPublisher.publishEvent(event);
    }
}
