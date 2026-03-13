package com.batrobot.player.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "players", indexes = {
    @Index(name = "idx_players_updated", columnList = "updated_at"),
    @Index(name = "idx_players_steam_id", columnList = "steam_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerEntity {
    
    @Id
    private UUID id;
    
    @Column(name = "steam_id", unique = true, nullable = false)
    private Long steamId;
    
    // Profile information
    private String avatarUrl;
    private String name;
    private String profileUrl;
    private Long accountCreationDate;
    
    // Visibility and privacy settings
    private Integer communityVisibleState;
    private Boolean isAnonymous;
    private Boolean isStratzPublic;

    // Flags
    private Boolean isDotaPlusSubscriber;
    private Integer smurfFlag;
        
    // Rank and activity
    private Integer seasonRank;
    private String activity;
    private Integer imp;  
    
    // Match statistics
    private Integer matchCount;
    private Integer winCount;
    private Long firstMatchDate;
    private Long lastMatchDate;
        
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    
    @Column(nullable = false)
    private OffsetDateTime updatedAt;
}

