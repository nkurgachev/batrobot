package com.batrobot.rankhistory.application.dto.request;

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
public class PlayerRankRequest {
    @NotNull(message = "Chat ID cannot be null")
    private Long steamId64;
    
    @NotNull(message = "Chat type cannot be null")
    private Integer seasonRank;
}
