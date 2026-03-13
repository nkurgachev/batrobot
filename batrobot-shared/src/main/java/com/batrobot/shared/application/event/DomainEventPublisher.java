package com.batrobot.shared.application.event;

import com.batrobot.shared.domain.event.DomainEvent;

/**
 * Domain publisher for publishing domain events.
 */
public interface DomainEventPublisher {

    /**
     * Publishes a domain event.
     * Event will be delivered to all registered handlers.
     * 
     * @param event the domain event to publish
     */
    void publish(DomainEvent event);
}