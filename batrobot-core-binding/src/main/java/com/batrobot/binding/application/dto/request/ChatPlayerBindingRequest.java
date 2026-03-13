package com.batrobot.binding.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for binding a Player to a Telegram user in a chat.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatPlayerBindingRequest {
    @NotNull(message = "Chat ID cannot be null")
    private Long telegramChatId;

    @NotNull(message = "Telegram User ID cannot be null")
    private Long telegramUserId;
    
    @NotNull(message = "Steam ID cannot be null")
    private Long steamId64;

    private boolean isPrimary;
}

