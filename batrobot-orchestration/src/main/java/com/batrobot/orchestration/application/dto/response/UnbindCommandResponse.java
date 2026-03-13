package com.batrobot.orchestration.application.dto.response;

/**
 * BFF Response for unbind command.
 */
public record UnbindCommandResponse(
    // Chat information
    Long chatId,

    // Telegram user information
    Long telegramUserId,
    String telegramUsername,

    // Player information
    Long steamId64,
    String steamUsername
) {
}
