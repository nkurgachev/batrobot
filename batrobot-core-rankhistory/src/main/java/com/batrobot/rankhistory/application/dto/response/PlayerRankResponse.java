package com.batrobot.rankhistory.application.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Response containing player rank history data.
 */
public record PlayerRankResponse(
    UUID id,
    Long steamId64,
    Integer seasonRank,
    OffsetDateTime assignedAt
) {
}
