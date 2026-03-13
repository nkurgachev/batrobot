package com.batrobot.user.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Telegram User received from Telegram Bot API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @NotNull(message = "Telegram User ID cannot be null")
    private Long telegramUserId;
    
    @NotNull(message = "Username cannot be null")
    private String username; 
    
    private String firstName;
    private String lastName;
}

