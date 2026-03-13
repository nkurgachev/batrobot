package com.batrobot.playerstats.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Player statistics within a specific match.
 * Contains player-specific performance data for a match.
 * 
 * Identity:
 * - id: Internal UUID-based primary key
 * - match_id + steam_id: External composite business key (unique constraint)
 */
@Entity
@Table(name = "player_match_stats", indexes = {
    @Index(name = "idx_pms_steam_id", columnList = "steam_id"),
    @Index(name = "idx_pms_match_id", columnList = "match_id"),
    @Index(name = "idx_pms_steam_match", columnList = "steam_id, match_id", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerMatchStatsEntity {
    
    @Id
    private UUID id;
    
    // Foreign key: Stratz API match ID (references matches.match_id, not matches.id)
    // This allows future separation of match and player_match_stats into different microservices
    @Column(name = "match_id", nullable = false)
    private Long matchId;
    
    // Denormalized Steam ID for independent player data access
    // No @ManyToOne relationship to allow future microservice separation
    @Column(name = "steam_id", nullable = false)
    private Long steamId;
    
    // Hero information
    private Integer heroId;
    private String heroName;
    
    // Game result for player
    private Boolean isVictory;
    private Boolean isRadiant;
    
    // KDA
    private Integer kills;
    private Integer deaths;
    private Integer assists;
    
    // Farming & Economy
    private Integer numLastHits;
    private Integer numDenies;
    private Integer goldPerMinute;
    private Integer experiencePerMinute;
    
    // Combat statistics
    private Integer heroDamage;
    private Integer towerDamage;
    private Integer heroHealing;
    
    // Positioning
    private String lane;
    private String position;
    
    // Performance metrics
    private Integer imp;
    private String award;
    
    // Support statistics
    private Integer campStack;
    private Integer courierKills;
    private Integer sentryWardsPurchased;
    private Integer observerWardsPurchased;
    private Integer sentryWardsDestroyed;
    private Integer observerWardsDestroyed;
    
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    
    @Column(nullable = false)
    private OffsetDateTime updatedAt;
}



