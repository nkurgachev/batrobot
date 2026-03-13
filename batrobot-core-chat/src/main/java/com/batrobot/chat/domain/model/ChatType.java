package com.batrobot.chat.domain.model;

import java.util.Objects;

import com.batrobot.chat.domain.exception.InvalidChatTypeException;

/**
 * Value Object representing Telegram chat type.
 */
public enum ChatType {
    PRIVATE("Private chat (1-to-1)"),
    GROUP("Group chat"),
    SUPERGROUP("Supergroup chat");
    
    private final String description;
    
    ChatType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Factory method to create ChatType from string.
     * 
     * @param value String value (case-insensitive)
     * @return ChatType instance
     * @throws InvalidChatTypeException if value is invalid
     */
    public static ChatType of(String value) {
        Objects.requireNonNull(value, "Chat type cannot be null");
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidChatTypeException(
                String.format("Invalid chat type: %s. Must be one of: PRIVATE, GROUP, SUPERGROUP", value)
            );
        }
    }
}

