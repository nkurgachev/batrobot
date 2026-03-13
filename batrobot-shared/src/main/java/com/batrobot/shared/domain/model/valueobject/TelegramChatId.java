package com.batrobot.shared.domain.model.valueobject;

import com.batrobot.shared.domain.exception.valueobject.InvalidTelegramChatIdException;

/**
 * Value Object representing Telegram chat ID.
 */
public record TelegramChatId(Long value) {
    public static final long MIN_TELEGRAM_CHAT_ID = 1;
    public static final long MIN_PRIVATE_CHAT_ID = 1; // Private chats have positive IDs

    public TelegramChatId {
        if (value == null) {
            throw new InvalidTelegramChatIdException("Telegram Chat ID cannot be null");
        }
        
        if (value < MIN_TELEGRAM_CHAT_ID && value > -MIN_PRIVATE_CHAT_ID) {
            throw new InvalidTelegramChatIdException(
                String.format("Telegram Chat ID must be positive or < -%d, got %d", MIN_PRIVATE_CHAT_ID, value)
            );
        }
    }

    /**
     * Factory method to create TelegramChatId.
     * @param value Telegram chat ID
     * @return TelegramChatId instance
     * @throws InvalidTelegramChatIdException if ID is invalid
     */
    public static TelegramChatId of(Long value) {
        return new TelegramChatId(value);
    }

    /**
     * Checks if this is a private chat (ID > 0).
     */
    public boolean isPrivate() {
        return value > 0;
    }

    /**
     * Checks if this is a group/supergroup chat (ID < 0).
     */
    public boolean isGroup() {
        return value < 0;
    }
}
