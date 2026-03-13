package com.batrobot.binding.domain.exception;

import com.batrobot.shared.domain.exception.DomainException;
import com.batrobot.shared.domain.model.valueobject.TelegramUserId;

import lombok.Getter;

/**
 * Thrown when attempting to bind a player that is already bound to another user.
 */
@Getter
public class PlayerAlreadyBoundException extends DomainException {
    
    private final TelegramUserId telegramUserId;

    public PlayerAlreadyBoundException(TelegramUserId telegramUserId) {
        super("Player already bound to userId: " + telegramUserId);
        this.telegramUserId = telegramUserId;
    }
}

