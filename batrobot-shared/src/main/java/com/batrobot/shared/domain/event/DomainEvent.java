package com.batrobot.shared.domain.event;

import java.time.OffsetDateTime;

/**
 * Base interface for all domain events.
 */
public interface DomainEvent {

    /**
     * Returns when this event occurred.
     */
    OffsetDateTime occurredAt();

    /**
     * Aggregate ID that caused this event (e.g., SteamId, ChatSteamBindingId).
     */
    Object aggregateId();

    /**
     * Event type identifier for routing and filtering.
     */
    String eventType();
}
