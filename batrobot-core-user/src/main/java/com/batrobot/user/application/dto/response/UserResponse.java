package com.batrobot.user.application.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Response containing Telegram user data.
 */
public record UserResponse(
    UUID id,
    Long telegramUserId,
    String username,
    String firstName,
    String lastName,
    String emoji,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
}

