package com.batrobot.rankhistory.domain.model;

import com.batrobot.shared.domain.model.BaseAggregateRoot;
import com.batrobot.shared.domain.model.valueobject.SeasonRank;
import com.batrobot.shared.domain.model.valueobject.SteamId;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Domain Entity: PlayerRankHistory.
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(of = { "id", "steamId", "seasonRank", "assignedAt" })
public class PlayerRankHistory extends BaseAggregateRoot {

    // === Identity ===
    private final UUID id;
    private final SteamId steamId;

    private final SeasonRank seasonRank;
    private final OffsetDateTime assignedAt;

    /**
     * Constructor for creating new Player with all fields.
     */
    private PlayerRankHistory(
            SteamId steamId,
            SeasonRank seasonRank) {

        this.id = UUID.randomUUID();

        this.steamId = steamId;
        this.seasonRank = seasonRank;
        this.assignedAt = OffsetDateTime.now();
    }

    /**
     * Constructor for reconstitution from persistence layer.
     */
    private PlayerRankHistory(
            UUID id,
            SteamId steamId,
            SeasonRank seasonRank,
            OffsetDateTime assignedAt) {

        this.id = id;
        this.steamId = steamId;
        this.seasonRank = seasonRank;
        this.assignedAt = assignedAt;
    }

    // ==================== Factory Methods ====================

    public static PlayerRankHistory create(
            SteamId steamId,
            SeasonRank seasonRank) {
        PlayerRankHistory rankHistory = new PlayerRankHistory(
                steamId,
                seasonRank);
        return rankHistory;
    }

    /**
     * Reconstitutes rank history from persistence.
     */
    public static PlayerRankHistory reconstitute(
            UUID id,
            SteamId steamId,
            SeasonRank seasonRank,
            OffsetDateTime assignedAt) {
        return new PlayerRankHistory(
                id,
                steamId,
                seasonRank,
                assignedAt);
    }

    // ==================== Business Methods ====================
}

