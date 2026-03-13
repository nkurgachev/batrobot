package com.batrobot.match.application.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Response containing Match data.
 */
public record MatchResponse(
    UUID id,
    Long matchId,
    Integer durationSeconds,

    Long startDateTime,
    Long endDateTime,
    String lobbyType,
    String gameMode,

    Integer actualRank,
    Integer radiantKills,
    Integer direKills,

    // Lane outcomes
    String analysisOutcome,
    String bottomLaneOutcome,
    String midLaneOutcome,
    String topLaneOutcome,

    // Audit fields
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
}
