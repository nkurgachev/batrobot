package com.batrobot.orchestration.application.dto.request;

import com.batrobot.orchestration.application.dto.request.info.ChatInfo;
import com.batrobot.orchestration.application.dto.request.info.UserInfo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for fetching results of commands via orchestration layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonRequest {
    @Valid
    @NotNull(message = "Chat info cannot be null")
    private ChatInfo chat;

    @Valid
    @NotNull(message = "User info cannot be null")
    private UserInfo user;
}
