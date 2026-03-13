package com.batrobot.shared.domain.model.valueobject;

import com.batrobot.shared.domain.exception.valueobject.InvalidTelegramUserIdException;

/**
 * Value Object representing Telegram user ID.
 */
public record TelegramUserId(Long value) {
    public static final long MIN_TELEGRAM_USER_ID = 1;

    public TelegramUserId {
        if (value == null) {
            throw new InvalidTelegramUserIdException("Telegram User ID cannot be null");
        }

        if (value < MIN_TELEGRAM_USER_ID) {
            throw new InvalidTelegramUserIdException(
                String.format("Telegram User ID must be >= %d, got %d", MIN_TELEGRAM_USER_ID, value)
            );
        }
    }

    /**
     * Factory method to create TelegramUserId.
     * @param value Telegram user ID
     * @return TelegramUserId instance
     * @throws InvalidTelegramUserIdException if ID is invalid
     */
    public static TelegramUserId of(Long value) {
        return new TelegramUserId(value);
    }
}
