package com.batrobot.orchestration.application.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * BFF Response for bind command.
 */
public record BindCommandResponse(
    // Binding information
    UUID bindingId,
    boolean isPrimary,
    
    // Chat information
    Long telegramChatId,
    String telegramChatTitle,
    
    // User information
    Long telegramUserId,
    String telegramUsername,
    
    // Player information (essential for Dota 2 context)
    Long steamId64,
    String steamUsername,
    
    // Metadata
    OffsetDateTime createdAt
) {
}
