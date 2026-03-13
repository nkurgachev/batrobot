package com.batrobot.rankhistory.application.dto.response;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response containing player rank history data with nested rank entries.
 */
public record PlayerRankHistoryResponse(
    Long steamId64,
    List<Rank> rankHistory
) {
    /**
     * Individual rank history entry (season rank and assignment date).
     */
    public record Rank(
        UUID id,
        Integer seasonRank,
        OffsetDateTime assignedAt
    ) {
    }
} 



