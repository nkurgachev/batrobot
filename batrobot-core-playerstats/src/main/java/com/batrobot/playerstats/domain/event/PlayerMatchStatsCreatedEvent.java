package com.batrobot.playerstats.domain.event;

import com.batrobot.shared.domain.event.DomainEvent;
import com.batrobot.shared.domain.model.valueobject.MatchId;
import com.batrobot.shared.domain.model.valueobject.SteamId;

import java.time.OffsetDateTime;

/**
 * Domain event that is published when new player match information is added to the system.
 */
public record PlayerMatchStatsCreatedEvent(
    MatchId matchId,
    SteamId steamId,
    String heroName,
    Boolean isVictory,
    Integer kills,
    Integer deaths,
    Integer assists,
    String position,
    String award,
    Integer imp,
    OffsetDateTime occurredAt
) implements DomainEvent {

    public PlayerMatchStatsCreatedEvent(
            MatchId matchId, SteamId steamId,
            String heroName, Boolean isVictory,
            Integer kills, Integer deaths, Integer assists,
            String position, String award, Integer imp) {
        this(matchId, steamId, heroName, isVictory, kills, deaths, assists,
                position, award, imp, OffsetDateTime.now());
    }

    @Override
    public Object aggregateId() {
        return steamId;
    }

    @Override
    public String eventType() {
        return "playerstats.created";
    }
}


