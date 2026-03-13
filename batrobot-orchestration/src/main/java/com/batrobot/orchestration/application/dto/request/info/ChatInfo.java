package com.batrobot.orchestration.application.dto.request.info;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Chat information for orchestration requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatInfo {

    @NotNull(message = "Chat ID cannot be null")
    private Long telegramChatId;

    @NotNull(message = "Chat type cannot be null")
    private String type;

    @NotNull(message = "Chat title cannot be null")
    private String title;
}
