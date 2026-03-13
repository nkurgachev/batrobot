package com.batrobot.playerstats.domain.exception;

import com.batrobot.shared.domain.exception.DomainException;

/**
 * KDA values must be non-negative integers.
 */
public class InvalidPlayerKdaException extends DomainException {
    
    public InvalidPlayerKdaException(String message) {
        super(message);
    }
    
    public InvalidPlayerKdaException(String message, Throwable cause) {
        super(message, cause);
    }
}

