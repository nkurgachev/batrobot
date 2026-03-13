package com.batrobot.binding.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.batrobot.binding.domain.event.PlayerBindingCreatedEvent;
import com.batrobot.shared.domain.model.BaseAggregateRoot;
import com.batrobot.shared.domain.model.valueobject.*;

/**
 * Domain Entity: ChatPlayerBinding (Aggregate Root)
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(of = { "id", "chatId", "userId", "steamId" })
public class ChatPlayerBinding extends BaseAggregateRoot {

    // === Identity ===
    private final UUID id;

    // === Aggregated Entities (references, not full entities) ===
    private final TelegramChatId chatId;
    private final TelegramUserId userId;
    private final SteamId steamId;

    // === ChatPlayerBinding properties ===
    private Boolean isPrimary;
    private String notificationSettings;

    // === Audit fields ===
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    /**
     * Constructor for creating new ChatPlayerBinding with all fields.
     */
    private ChatPlayerBinding(
            TelegramChatId chatId,
            TelegramUserId userId,
            SteamId steamId,
            Boolean isPrimary,
            String notificationSettings) {
        this.id = UUID.randomUUID();
        this.chatId = chatId;
        this.userId = userId;
        this.steamId = steamId;
        this.isPrimary = isPrimary;
        this.notificationSettings = notificationSettings;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * Constructor for reconstitution from persistence layer.
     */
    private ChatPlayerBinding(
            UUID id,
            TelegramChatId chatId,
            TelegramUserId userId,
            SteamId steamId,
            Boolean isPrimary,
            String notificationSettings,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt) {
        this.id = id;
        this.chatId = chatId;
        this.userId = userId;
        this.steamId = steamId;
        this.isPrimary = isPrimary;
        this.notificationSettings = notificationSettings;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ==================== Factory Methods ====================

    /**
     * Creates a new binding between chat, user, and steam account.
     */
    public static ChatPlayerBinding create(
            TelegramChatId chatId,
            TelegramUserId userId,
            SteamId steamId,
            Boolean isPrimary) {
        ChatPlayerBinding binding = new ChatPlayerBinding(
                chatId,
                userId,
                steamId,
                isPrimary,
                null);

        binding.registerEvent(
                new PlayerBindingCreatedEvent(
                        binding.id,
                        binding.chatId,
                        binding.userId,
                        binding.steamId));

        return binding;
    }

    /**
     * Reconstructs from persistence layer.
     */
    public static ChatPlayerBinding reconstitute(
            UUID id,
            TelegramChatId chatId,
            TelegramUserId userId,
            SteamId steamId,
            Boolean isPrimary,
            String notificationSettings,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt) {
        return new ChatPlayerBinding(
                id,
                chatId,
                userId,
                steamId,
                isPrimary,
                notificationSettings,
                createdAt,
                updatedAt);
    }

    // ==================== Business Methods ====================

    /**
     * Marks this binding as primary (preferred account for this user in this chat).
     */
    public void markAsPrimary() {
        this.isPrimary = true;
        this.updatedAt = OffsetDateTime.now();
    }

    /**
     * Unmarks this binding as primary.
     */
    public void unmarkAsPrimary() {
        this.isPrimary = false;
        this.updatedAt = OffsetDateTime.now();
    }

    /**
     * Updates notification settings for this binding.
     */
    public boolean updateNotificationSettings(String newNotificationSettings) {
        boolean changed = false;

        changed |= updateField(newNotificationSettings, this.notificationSettings, val -> this.notificationSettings = val);

        if (changed) {
            this.updatedAt = OffsetDateTime.now();
        }

        return changed;
    }

    // ==================== Query Methods ====================

    /**
     * Checks if this is the primary binding for the user in the chat.
     */
    public boolean isPrimary() {
        return isPrimary != null && isPrimary;
    }

    /**
     * Checks if notifications are enabled for this binding.
     */
    public boolean hasNotifications() {
        return notificationSettings != null && !notificationSettings.isEmpty();
    }
}

