package com.batrobot.playerstats.domain.exception;

import com.batrobot.shared.domain.exception.DomainException;

/**
 * Player economy values (last hits, denies, GPM, XPM) must be non-negative integers.
 */
public class InvalidPlayerEconomyException extends DomainException {
    
    public InvalidPlayerEconomyException(String message) {
        super(message);
    }
    
    public InvalidPlayerEconomyException(String message, Throwable cause) {
        super(message, cause);
    }
}

