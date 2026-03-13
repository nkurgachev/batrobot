package com.batrobot.binding.application.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object representing a Player binding in a chat context.
 */
public record ChatPlayerBindingResponse(
    UUID id,

    Long chatId,
    Long telegramUserId,
    Long steamId64,

    boolean isPrimary,
    String notificationSettings,

    // Audit fields
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
}

