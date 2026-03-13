package com.batrobot.bot.infrastructure.telegram.command.exception;

import java.util.Objects;

import lombok.Getter;

/**
 * Exception for command input parsing and syntax errors in telegram layer.
 */
@Getter
public class TelegramCommandInputException extends RuntimeException {

    private final String messageKey;
    private final Object[] messageArgs;

    public TelegramCommandInputException(String messageKey, Object... messageArgs) {
        super("Telegram command input error: " + Objects.requireNonNull(messageKey));
        this.messageKey = messageKey;
        this.messageArgs = messageArgs != null ? messageArgs.clone() : new Object[0];
    }
}
