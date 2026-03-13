package com.batrobot.shared.domain.exception.valueobject;

import com.batrobot.shared.domain.exception.DomainException;

/**
 * Thrown when an invalid Steam ID is provided to SteamId value object.
 */
public class InvalidSteamIdException extends DomainException {
    
    public InvalidSteamIdException(String message) {
        super(message);
    }
    
    public InvalidSteamIdException(String message, Throwable cause) {
        super(message, cause);
    }
}
