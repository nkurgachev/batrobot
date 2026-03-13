package com.batrobot.orchestration.application.dto.request;

import com.batrobot.orchestration.application.dto.request.info.ChatInfo;
import com.batrobot.orchestration.application.dto.request.info.UserInfo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a player binding via orchestration layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BindCommandRequest {

    @Valid
    @NotNull(message = "Chat info cannot be null")
    private ChatInfo chat;

    @Valid
    @NotNull(message = "User info cannot be null")
    private UserInfo user;

    @NotNull(message = "Steam ID cannot be null")
    @Positive(message = "Steam ID must be positive")
    private Long steamId64;

    private boolean isPrimaryBinding;
}
