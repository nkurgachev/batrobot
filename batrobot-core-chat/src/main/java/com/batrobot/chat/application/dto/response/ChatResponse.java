package com.batrobot.chat.application.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Response containing Telegram chat data.
 */
public record ChatResponse(
    UUID id,
    
    Long chatId,
    String type,
    String title,

    // Audit fields
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
}

