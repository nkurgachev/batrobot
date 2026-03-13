package com.batrobot.rankhistory.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Player rank history record for a specific season.
 */
@Entity
@Table(name = "player_rank_history", indexes = {
    @Index(name = "idx_prh_steam_assigned", columnList = "steam_id, assigned_at"),
    @Index(name = "idx_prh_assigned", columnList = "assigned_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerRankHistoryEntity {

    @Id
    private UUID id;

    @Column(name = "steam_id", nullable = false)
    private Long steamId;

    @Column(nullable = false)
    private Integer seasonRank;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime assignedAt;
}

