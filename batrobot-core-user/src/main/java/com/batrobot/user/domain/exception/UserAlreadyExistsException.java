package com.batrobot.user.domain.exception;

import com.batrobot.shared.domain.exception.DomainException;
import com.batrobot.shared.domain.model.valueobject.TelegramUserId;

import lombok.Getter;

/**
 * Thrown when attempting to create a user that already exists.
 */
@Getter
public class UserAlreadyExistsException extends DomainException {

    private final TelegramUserId telegramUserId;

    public UserAlreadyExistsException(TelegramUserId telegramUserId) {
        super("User with ID " + telegramUserId + " already exists");
        this.telegramUserId = telegramUserId;
    }
}

