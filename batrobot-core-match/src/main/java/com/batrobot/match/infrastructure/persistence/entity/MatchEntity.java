package com.batrobot.match.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Match entity representing general match data shared by all players.
 */
@Entity
@Table(name = "matches", indexes = {
    @Index(name = "idx_match_start_datetime", columnList = "start_date_time")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchEntity {
    
    @Id
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private Long matchId;
    
    private Integer durationSeconds;
    
    private Long startDateTime;
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
    
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    
    @Column(nullable = false)
    private OffsetDateTime updatedAt;
}



