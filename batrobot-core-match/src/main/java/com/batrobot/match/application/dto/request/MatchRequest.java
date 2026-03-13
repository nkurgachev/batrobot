package com.batrobot.match.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Match data received from external sources.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchRequest {
    @NotNull(message = "Match ID cannot be null")
    private Long matchId;
    private Integer durationSeconds;

    @NotNull(message = "Start date and time cannot be null")
    private Long startDateTime;
    @NotNull(message = "End date and time cannot be null")
    private Long endDateTime;
    private String lobbyType;
    private String gameMode;

    private Integer actualRank;
    private Integer radiantKills;
    private Integer direKills;

    // Lane outcomes
    private String analysisOutcome;
    private String bottomLaneOutcome;
    private String midLaneOutcome;
    private String topLaneOutcome;
}
