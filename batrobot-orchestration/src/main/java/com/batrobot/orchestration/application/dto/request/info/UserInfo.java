package com.batrobot.orchestration.application.dto.request.info;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User information for orchestration requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    @NotNull(message = "Telegram User ID cannot be null")
    private Long telegramUserId;

    @NotNull(message = "Username cannot be null")
    private String username;

    private String firstName;
    private String lastName;
}
