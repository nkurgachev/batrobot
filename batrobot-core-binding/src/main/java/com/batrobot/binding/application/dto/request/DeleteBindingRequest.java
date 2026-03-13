package com.batrobot.binding.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for deleting a player binding.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteBindingRequest {
    
    @NotNull(message = "Chat ID cannot be null")
    private Long chatId;
    
    @NotNull(message = "User ID cannot be null")
    private Long userId;
    
    @NotNull(message = "Steam ID cannot be null")
    private Long steamId64;
}

