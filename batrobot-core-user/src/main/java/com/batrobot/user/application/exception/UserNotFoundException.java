package com.batrobot.user.application.exception;

import com.batrobot.shared.application.exception.ApplicationException;
import com.batrobot.shared.domain.model.valueobject.TelegramUserId;

import lombok.Getter;

/**
 * Thrown when Telegram user is not found.
 */
@Getter
public class UserNotFoundException extends ApplicationException {

    private final TelegramUserId telegramUserId;

    public UserNotFoundException(TelegramUserId telegramUserId) {
        super("Telegram user not found: " + telegramUserId);
        this.telegramUserId = telegramUserId;
    }
}