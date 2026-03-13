package com.batrobot.shared.domain.exception.valueobject;

import com.batrobot.shared.domain.exception.DomainException;

/**
 * Thrown when an invalid Telegram Chat ID is provided to TelegramChatId value object.
 */
public class InvalidTelegramChatIdException extends DomainException {
    
    public InvalidTelegramChatIdException(String message) {
        super(message);
    }
    
    public InvalidTelegramChatIdException(String message, Throwable cause) {
        super(message, cause);
    }
}
