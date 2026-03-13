package com.batrobot.playerstats.domain.exception;

import com.batrobot.shared.domain.exception.DomainException;

/**
 * Player combat values (kills, deaths, assists) must be non-negative integers.
 */
public class InvalidPlayerCombatException extends DomainException {
    
    public InvalidPlayerCombatException(String message) {
        super(message);
    }
    
    public InvalidPlayerCombatException(String message, Throwable cause) {
        super(message, cause);
    }
}

