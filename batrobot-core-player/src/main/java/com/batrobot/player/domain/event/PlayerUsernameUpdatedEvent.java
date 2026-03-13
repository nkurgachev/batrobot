package com.batrobot.player.domain.event;

import com.batrobot.shared.domain.event.DomainEvent;
import com.batrobot.shared.domain.model.valueobject.SteamId;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Domain event: Player's username has changed.
 */
public record PlayerUsernameUpdatedEvent(
        UUID id,
        SteamId steamId,
        String newSteamUsername,
        String oldSteamUsername,
        OffsetDateTime occurredAt) implements DomainEvent {

    public PlayerUsernameUpdatedEvent(UUID id, SteamId steamId, String newUsername, String oldUsername) {
        this(id,
                steamId,
                newUsername,
                oldUsername,
                OffsetDateTime.now());
    }

    @Override
    public Object aggregateId() {
        return id;
    }

    @Override
    public String eventType() {
        return "player.steam_username_changed";
    }
}

