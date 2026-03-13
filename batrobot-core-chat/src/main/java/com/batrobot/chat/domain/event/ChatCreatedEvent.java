package com.batrobot.chat.domain.event;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.batrobot.chat.domain.model.ChatType;
import com.batrobot.shared.domain.event.DomainEvent;
import com.batrobot.shared.domain.model.valueobject.TelegramChatId;

/**
 * Domain event: Chat has been created.
 */
public record ChatCreatedEvent(
    UUID id,
    TelegramChatId telegramChatId,
    ChatType type,
    String title,
    OffsetDateTime occurredAt
) implements DomainEvent {

    public ChatCreatedEvent(
            UUID id,
            TelegramChatId telegramChatId,
            ChatType type,
            String title
    ) {
        this(id, telegramChatId, type, title, OffsetDateTime.now());
    }

    @Override
    public Object aggregateId() {
        return id;
    }

    @Override
    public String eventType() {
        return "chat.created";
    }
}
