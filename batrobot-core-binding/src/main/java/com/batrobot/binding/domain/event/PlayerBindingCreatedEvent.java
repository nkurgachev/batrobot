package com.batrobot.binding.domain.event;

import com.batrobot.shared.domain.event.DomainEvent;
import com.batrobot.shared.domain.model.valueobject.SteamId;
import com.batrobot.shared.domain.model.valueobject.TelegramChatId;
import com.batrobot.shared.domain.model.valueobject.TelegramUserId;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Domain event: Player binding has been created.
 */
public record PlayerBindingCreatedEvent(
        UUID id,
        TelegramChatId chatId,
        TelegramUserId userId,
        SteamId steamId,
        OffsetDateTime occurredAt) implements DomainEvent {

    public PlayerBindingCreatedEvent(UUID id, TelegramChatId chatId, TelegramUserId userId, SteamId steamId) {
        this(id, chatId, userId, steamId, OffsetDateTime.now());
    }

    @Override
    public Object aggregateId() {
        return id;
    }

    @Override
    public String eventType() {
        return "binding.created";
    }
}
