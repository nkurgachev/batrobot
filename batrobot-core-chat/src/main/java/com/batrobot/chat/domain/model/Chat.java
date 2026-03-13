package com.batrobot.chat.domain.model;

import lombok.Getter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.batrobot.chat.domain.event.ChatCreatedEvent;
import com.batrobot.shared.domain.model.BaseAggregateRoot;
import com.batrobot.shared.domain.model.valueobject.TelegramChatId;

/**
 * Domain Entity representing Telegram chat.
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(of = { "id", "telegramChatId", "type", "title" })
public class Chat extends BaseAggregateRoot {

    // === Identity ===
    private final UUID id;
    private final TelegramChatId telegramChatId;

    // === Chat properties ===

    private ChatType type;
    private String title;

    // === Audit fields ===
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    /**
     * Constructor for creating new Chat with all fields.
     */
    private Chat(
            TelegramChatId telegramChatId,
            ChatType type,
            String title) {
        this.id = UUID.randomUUID();
        this.telegramChatId = telegramChatId;
        this.type = type;
        this.title = title;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * Constructor for reconstitution from persistence layer.
     */
    private Chat(
            UUID id,
            TelegramChatId telegramChatId,
            ChatType type,
            String title,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt) {
        this.id = id;
        this.telegramChatId = telegramChatId;
        this.type = type;
        this.title = title;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ==================== Factory Methods ====================

    /**
     * Creates a new Telegram chat.
     */
    public static Chat create(
            TelegramChatId telegramChatId,
            ChatType type,
            String title) {
        Chat chat = new Chat(
                telegramChatId,
                type,
                title);

        chat.registerEvent(
                new ChatCreatedEvent(
                        chat.id,
                        chat.telegramChatId,
                        chat.type,
                        chat.title));

        return chat;
    }

    /**
     * Reconstitutes Telegram chat from persistence layer.
     */
    public static Chat reconstitute(
            UUID id,
            TelegramChatId telegramChatId,
            ChatType type,
            String title,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt) {
        Chat chat = new Chat(
                id,
                telegramChatId,
                type,
                title,
                createdAt,
                updatedAt);
        return chat;
    }

    // ==================== Business Methods ====================

    /**
     * Updates chat title. Returns true if the title was changed.
     *
     * @param newTitle New chat title
     * @return true if changed, false otherwise
     */
    public boolean updateTitle(String newTitle) {
        boolean changed = false;

        changed |= updateField(newTitle, this.title, val -> this.title = val);

        if (changed) {
            this.updatedAt = OffsetDateTime.now();
        }

        return changed;
    }

    // ==================== Query Methods ====================

    /**
     * Checks if this is a private chat (1-to-1).
     * 
     * @return true if chat is private, false otherwise
     */
    public boolean isPrivate() {
        return type == ChatType.PRIVATE;
    }

    /**
     * Checks if this is a group chat.
     * 
     * @return true if chat is group or supergroup, false otherwise
     */
    public boolean isGroup() {
        return type == ChatType.GROUP || type == ChatType.SUPERGROUP;
    }

    /**
     * Checks if this is a supergroup.
     * 
     * @return true if chat is supergroup, false otherwise
     */
    public boolean isSupergroup() {
        return type == ChatType.SUPERGROUP;
    }

    /**
     * Gets chat display name/title.
     * 
     * @return Chat title or default name based on type
     */
    public String getDisplayName() {
        if (title != null && !title.isEmpty()) {
            return title;
        }

        return switch (type) {
            case PRIVATE -> "Private Chat";
            case GROUP -> "Group";
            case SUPERGROUP -> "Supergroup";
        };
    }
}

