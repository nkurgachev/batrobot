package com.batrobot.user.domain.model;

import com.batrobot.shared.domain.model.BaseAggregateRoot;
import com.batrobot.shared.domain.model.valueobject.TelegramUserId;

import lombok.Getter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Domain Entity representing Telegram user.
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(of = { "id", "telegramUserId", "username", "firstName", "lastName" })
public class User extends BaseAggregateRoot {

    public static final String DEFAULT_EMOJI = "👤";

    // === Identity ===
    private final UUID id;
    private final TelegramUserId telegramUserId;

    // === User properties ===
    
    private String username;
    private String firstName;
    private String lastName;
    private String emoji;

    // === Audit fields ===
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    /**
     * Constructor for creating new TelegramUser.
     */
    private User(
            TelegramUserId telegramUserId,
            String username,
            String firstName,
            String lastName) {
        this.id = UUID.randomUUID();

        this.telegramUserId = telegramUserId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emoji = DEFAULT_EMOJI;

        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * Constructor for reconstitution from persistence layer.
     */
    private User(
            UUID id,
            TelegramUserId telegramUserId,
            String username,
            String firstName,
            String lastName,
            String emoji,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt) {
        this.id = id;

        this.telegramUserId = telegramUserId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emoji = normalizeEmoji(emoji);

        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ==================== Factory Methods ====================

    /**
     * Creates a new Telegram user.
     */
    public static User create(
            TelegramUserId telegramUserId,
            String username,
            String firstName,
            String lastName) {
        User user = new User(
                telegramUserId,
                username,
                firstName,
                lastName);

        return user;
    }

    /**
     * Reconstitutes Telegram user from persistence layer.
     */
    public static User reconstitute(
            UUID id,
            TelegramUserId telegramUserId,
            String username,
            String firstName,
            String lastName,
            String emoji,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt) {
        return new User(
                id,
                telegramUserId,
                username,
                firstName,
                lastName,
                emoji,
                createdAt,
                updatedAt);
    }

    // ==================== Business Methods ====================

    /**
     * Updates user's username. Returns true if entity was changed.
     */
    public boolean updateUsername(String newUsername) {
        boolean changed = false;

        changed |= updateField(newUsername, this.username, val -> this.username = val);

        if (changed) {
            this.updatedAt = OffsetDateTime.now();
        }
        return changed;
    }

    /**
     * Updates user's bio information (first and last name). Returns true if entity was changed.
     */
    public boolean updateBio(String newFirstName, String newLastName) {
        boolean changed = false;

        changed |= updateField(newFirstName, this.firstName, val -> this.firstName = val);
        changed |= updateField(newLastName, this.lastName, val -> this.lastName = val);

        if (changed) {
            this.updatedAt = OffsetDateTime.now();
        }
        return changed;
    }

    /**
     * Updates user's preferred emoji. Returns true if entity was changed.
     */
    public boolean updateEmoji(String newEmoji) {
        String normalized = normalizeEmoji(newEmoji);
        boolean changed = updateField(normalized, this.emoji, val -> this.emoji = val);

        if (changed) {
            this.updatedAt = OffsetDateTime.now();
        }

        return changed;
    }

    // ==================== Query Methods ====================

    /**
     * Gets display name for the user.
     * Returns first + last name if available, otherwise username, or "Unknown".
     * 
     * @return User's display name (full name or username)
     */
    public String getDisplayName() {
        if (firstName != null || lastName != null) {
            String first = firstName != null ? firstName : "";
            String last = lastName != null ? lastName : "";
            String fullName = (first + " " + last).trim();
            return !fullName.isEmpty() ? fullName : (username != null ? username : "Unknown");
        }
        return username != null ? username : "Unknown";
    }

    private static String normalizeEmoji(String emoji) {
        if (emoji == null || emoji.isBlank()) {
            return DEFAULT_EMOJI;
        }
        return emoji;
    }
}

