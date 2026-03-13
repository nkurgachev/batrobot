package com.batrobot.shared.domain.exception.valueobject;

import com.batrobot.shared.domain.exception.DomainException;

/**
 * Thrown when an invalid Match ID is provided to MatchId value object.
 */
public class InvalidMatchIdException extends DomainException {
    
    public InvalidMatchIdException(String message) {
        super(message);
    }
    
    public InvalidMatchIdException(String message, Throwable cause) {
        super(message, cause);
    }
}
