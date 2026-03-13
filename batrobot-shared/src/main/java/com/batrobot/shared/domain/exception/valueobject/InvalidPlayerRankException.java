package com.batrobot.shared.domain.exception.valueobject;

import com.batrobot.shared.domain.exception.DomainException;

/**
 * Thrown when Player Rank is invalid.
 */
public class InvalidPlayerRankException extends DomainException {
    
    public InvalidPlayerRankException(String message) {
        super(message);
    }
    
    public InvalidPlayerRankException(String message, Throwable cause) {
        super(message, cause);
    }
}
