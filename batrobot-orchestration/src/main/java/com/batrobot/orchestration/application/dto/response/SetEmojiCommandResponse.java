package com.batrobot.orchestration.application.dto.response;

/**
 * BFF response for set_emoji command.
 */
public record SetEmojiCommandResponse(
        Long telegramUserId,
        String telegramUsername,
        String emoji) {
}
