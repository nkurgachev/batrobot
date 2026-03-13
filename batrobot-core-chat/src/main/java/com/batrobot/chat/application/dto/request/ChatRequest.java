package com.batrobot.chat.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Telegram Chat received from Telegram Bot API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    @NotNull(message = "Chat ID cannot be null")
    private Long telegramChatId;
    
    @NotNull(message = "Chat type cannot be null")
    private String type;
    
    @NotNull(message = "Chat title cannot be null")
    private String title;
}

