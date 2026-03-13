package com.batrobot.shared.domain.exception.valueobject;

import com.batrobot.shared.domain.exception.DomainException;

/**
 * Thrown when an invalid Telegram User ID is provided to TelegramUserId value object.
 */
public class InvalidTelegramUserIdException extends DomainException {
    public InvalidTelegramUserIdException(String message) {
        super(message);
    }
    
    public InvalidTelegramUserIdException(String message, Throwable cause) {
        super(message, cause);
    }
}