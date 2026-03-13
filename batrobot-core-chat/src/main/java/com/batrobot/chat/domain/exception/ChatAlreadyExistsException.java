package com.batrobot.chat.domain.exception;

import com.batrobot.shared.domain.exception.DomainException;
import com.batrobot.shared.domain.model.valueobject.TelegramChatId;

import lombok.Getter;

/**
 * Thrown when attempting to create a chat that already exists.
 */
@Getter
public class ChatAlreadyExistsException extends DomainException {
    
    private final TelegramChatId telegramChatId;

    public ChatAlreadyExistsException(TelegramChatId telegramChatId) {
        super("Chat with ID " + telegramChatId + " already exists");
        this.telegramChatId = telegramChatId;
    }
}

