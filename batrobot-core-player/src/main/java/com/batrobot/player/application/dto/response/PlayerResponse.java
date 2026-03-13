package com.batrobot.player.application.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Response containing Player data.
 */
public record PlayerResponse(
    UUID id,
    Long steamId64,

    // Profile information
    String avatarUrl,
    String steamUsername,
    String profileUrl,
    Long accountCreationDate,
    
    // Visibility and privacy settings
    Integer communityVisibleState,
    Boolean isAnonymous,
    Boolean isStratzPublic,

    // Flags
    Boolean isDotaPlusSubscriber,
    Integer smurfFlag,

    // Rank and activity
    Integer seasonRank,
    String activity,
    Integer imp,

    // Match statistics
    Integer matchCount,
    Integer winCount,
    Long firstMatchDate,
    Long lastMatchDate,

    // Audit fields
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
 ) {
}
