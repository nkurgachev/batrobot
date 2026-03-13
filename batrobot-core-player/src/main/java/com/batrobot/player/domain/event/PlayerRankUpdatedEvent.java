package com.batrobot.player.domain.event;

import com.batrobot.shared.domain.event.DomainEvent;
import com.batrobot.shared.domain.model.valueobject.SeasonRank;
import com.batrobot.shared.domain.model.valueobject.SteamId;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Domain event: player's seasonal rank has changed.
 */
public record PlayerRankUpdatedEvent(
        UUID id,
        SteamId steamId,
        String steamUsername,
        SeasonRank newSeasonRank,
        SeasonRank oldSeasonRank,
        OffsetDateTime occurredAt) implements DomainEvent {

    public PlayerRankUpdatedEvent(UUID id, SteamId steamId, String steamUserName, SeasonRank newSeasonRank, SeasonRank oldSeasonRank) {
        this(id,
                steamId,
                steamUserName,
                newSeasonRank,
                oldSeasonRank,
                OffsetDateTime.now());
    }

    @Override
    public Object aggregateId() {
        return id;
    }

    @Override
    public String eventType() {
        return "player.rank_changed";
    }
}

