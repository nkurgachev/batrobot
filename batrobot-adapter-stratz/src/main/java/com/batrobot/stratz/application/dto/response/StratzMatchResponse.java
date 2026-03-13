package com.batrobot.stratz.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Intermediate DTO representing a single match from Stratz API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StratzMatchResponse {
    // Match info
    private Long matchId;
    private Integer durationSeconds;
    private Long startDateTime;
    private Long endDateTime;
    private String lobbyType;
    private String gameMode;
    private Integer actualRank;
    private Integer radiantKills;
    private Integer direKills;
    private String analysisOutcome;
    private String bottomLaneOutcome;
    private String midLaneOutcome;
    private String topLaneOutcome;

    // Player stats within this match
    private StratzPlayerMatchStatsResponse playerStats;
}

