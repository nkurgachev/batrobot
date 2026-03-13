package com.batrobot.match.domain.model;

import com.batrobot.match.domain.event.MatchCreatedEvent;
import com.batrobot.shared.domain.model.BaseAggregateRoot;
import com.batrobot.shared.domain.model.valueobject.MatchId;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Domain Entity representing Dota 2 Match.
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(of = {"id", "matchId", "durationSeconds", "lobbyType", "gameMode"})
public class Match extends BaseAggregateRoot {
    
    // === Identity ===
    private final UUID id;
    private final MatchId matchId;

    // === Match properties ===
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
    
    // === Audit fields ===
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    
    /**
     * Constructor for creating new Match with all fields.
     */
    private Match(
            MatchId matchId,
            Integer durationSeconds,
            Long startDateTime,
            Long endDateTime,
            String lobbyType,
            String gameMode,
            Integer actualRank,
            Integer radiantKills,
            Integer direKills,
            String analysisOutcome,
            String bottomLaneOutcome,
            String midLaneOutcome,
            String topLaneOutcome
    ) {
        this.id = UUID.randomUUID();
        this.matchId = matchId;
        this.durationSeconds = durationSeconds;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.lobbyType = lobbyType;
        this.gameMode = gameMode;
        this.actualRank = actualRank;
        this.radiantKills = radiantKills;
        this.direKills = direKills;
        this.analysisOutcome = analysisOutcome;
        this.bottomLaneOutcome = bottomLaneOutcome;
        this.midLaneOutcome = midLaneOutcome;
        this.topLaneOutcome = topLaneOutcome;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }
    
    /**
     * Constructor for reconstitution from persistence layer.
     */
    private Match(
            UUID id,
            MatchId matchId,
            Integer durationSeconds,
            Long startDateTime,
            Long endDateTime,
            String lobbyType,
            String gameMode,
            Integer actualRank,
            Integer radiantKills,
            Integer direKills,
            String analysisOutcome,
            String bottomLaneOutcome,
            String midLaneOutcome,
            String topLaneOutcome,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.matchId = matchId;
        this.durationSeconds = durationSeconds;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.lobbyType = lobbyType;
        this.gameMode = gameMode;
        this.actualRank = actualRank;
        this.radiantKills = radiantKills;
        this.direKills = direKills;
        this.analysisOutcome = analysisOutcome;
        this.bottomLaneOutcome = bottomLaneOutcome;
        this.midLaneOutcome = midLaneOutcome;
        this.topLaneOutcome = topLaneOutcome;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // ==================== Factory Methods ====================
    
    /**
     * Creates a new Match with full data.
     */
    public static Match create(
            MatchId matchId,
            Integer durationSeconds,
            Long startDateTime,
            Long endDateTime,
            String lobbyType,
            String gameMode,
            Integer actualRank,
            Integer radiantKills,
            Integer direKills,
            String analysisOutcome,
            String bottomLaneOutcome,
            String midLaneOutcome,
            String topLaneOutcome
    ) {
        Match match = new Match(
                matchId,
                durationSeconds,
                startDateTime,
                endDateTime,
                lobbyType,
                gameMode,
                actualRank,
                radiantKills,
                direKills,
                analysisOutcome,
                bottomLaneOutcome,
                midLaneOutcome,
                topLaneOutcome
        );

        match.registerEvent(
            new MatchCreatedEvent(
                match.id,
                match.matchId,
                match.durationSeconds,
                match.startDateTime,
                match.endDateTime,
                match.lobbyType,
                match.gameMode,
                match.actualRank,
                match.radiantKills,
                match.direKills,
                match.analysisOutcome,
                match.bottomLaneOutcome,
                match.midLaneOutcome,
                match.topLaneOutcome
            )
        );

        return match;
    }
    
    /**
     * Reconstitutes Match from persistence layer.
     */
    public static Match reconstitute(
            UUID id,
            MatchId matchId,
            Integer durationSeconds,
            Long startDateTime,
            Long endDateTime,
            String lobbyType,
            String gameMode,
            Integer actualRank,
            Integer radiantKills,
            Integer direKills,
            String analysisOutcome,
            String bottomLaneOutcome,
            String midLaneOutcome,
            String topLaneOutcome,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        return new Match(
            id,
            matchId,
            durationSeconds,
            startDateTime,
            endDateTime,
            lobbyType,
            gameMode,
            actualRank,
            radiantKills,
            direKills,
            analysisOutcome,
            bottomLaneOutcome,
            midLaneOutcome,
            topLaneOutcome,
            createdAt,
            updatedAt
        );
    }
    
    // ==================== Business Methods ====================
    
    /**
     * Updates match timing information.
     */
    public boolean updateTimings(
        Long newStartDateTime,
        Long newEndDateTime,
        Integer newDurationSeconds
    ) {
        boolean changed = false;

        changed |= updateField(newStartDateTime, this.startDateTime, val -> this.startDateTime = val);
        changed |= updateField(newEndDateTime, this.endDateTime, val -> this.endDateTime = val);
        changed |= updateField(newDurationSeconds, this.durationSeconds, val -> this.durationSeconds = val);

        if (changed) {
            this.updatedAt = OffsetDateTime.now();
        }

        return changed;
    }
    
    /**
     * Updates match game information (mode, lobby type).
     */
    public boolean updateGameInfo(
        String newGameMode,
        String newLobbyType
    ) {
        boolean changed = false;

        changed |= updateField(newGameMode, this.gameMode, val -> this.gameMode = val);
        changed |= updateField(newLobbyType, this.lobbyType, val -> this.lobbyType = val);

        if (changed) {
            this.updatedAt = OffsetDateTime.now();
        }

        return changed;
    }
    
    /**
     * Updates match outcome information.
     */
    public boolean updateOutcome(
        Integer newActualRank,
        Integer newRadiantKills,
        Integer newDireKills
    ) {
        boolean changed = false;

        changed |= updateField(newActualRank, this.actualRank, val -> this.actualRank = val);
        changed |= updateField(newRadiantKills, this.radiantKills, val -> this.radiantKills = val);
        changed |= updateField(newDireKills, this.direKills, val -> this.direKills = val);

        if (changed) {
            this.updatedAt = OffsetDateTime.now();
        }

        return changed;
    }
    
    /**
     * Updates lane analysis information.
     */
    public boolean updateLaneAnalysis(
        String newAnalysis,
        String newBottom,
        String newMid,
        String newTop
    ) {
        boolean changed = false;

        changed |= updateField(newAnalysis, this.analysisOutcome, val -> this.analysisOutcome = val);
        changed |= updateField(newBottom, this.bottomLaneOutcome, val -> this.bottomLaneOutcome = val);
        changed |= updateField(newMid, this.midLaneOutcome, val -> this.midLaneOutcome = val);
        changed |= updateField(newTop, this.topLaneOutcome, val -> this.topLaneOutcome = val);

        if (changed) {
            this.updatedAt = OffsetDateTime.now();
        }

        return changed;
    }
    
    // ==================== Query Methods ====================
    
    /**
     * Calculates match duration in minutes.
     */
    public double getDurationMinutes() {
        if (durationSeconds == null) return 0;
        return durationSeconds / 60.0;
    }
    
    /**
     * Checks if match has complete timing information.
     */
    public boolean hasCompleteTimings() {
        return startDateTime != null && endDateTime != null && durationSeconds != null;
    }
    
    /**
     * Checks if match has lane analysis.
     */
    public boolean hasLaneAnalysis() {
        return analysisOutcome != null || bottomLaneOutcome != null || 
               midLaneOutcome != null || topLaneOutcome != null;
    }
    
    /**
     * Checks if match has kill information.
     */
    public boolean hasKillInfo() {
        return radiantKills != null && direKills != null;
    }
}

