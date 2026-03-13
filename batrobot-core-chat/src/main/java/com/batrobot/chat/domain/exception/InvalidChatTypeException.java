package com.batrobot.chat.domain.exception;

import com.batrobot.shared.domain.exception.DomainException;

/**
 * Chat Type must be one of valid enumeration values (SUPERGROUP, GROUP, etc.).
 */
public class InvalidChatTypeException extends DomainException {
    
    public InvalidChatTypeException(String message) {
        super(message);
    }
    
    public InvalidChatTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}

