package com.batrobot.playerstats.application.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Response containing Player Match Stats data.
 */
public record PlayerMatchStatsResponse(
    UUID id,
    Long matchId,
    Long steamId64,

    // Hero info
    Integer heroId,
    String heroName,

    // Game outcome
    Boolean isVictory,
    Boolean isRadiant,

    // Performance stats
    Integer kills,
    Integer deaths,
    Integer assists,

    Integer numLastHits,
    Integer numDenies,
    Integer goldPerMinute,
    Integer experiencePerMinute,

    Integer heroDamage,
    Integer towerDamage,
    Integer heroHealing,

    // Position info
    String lane,
    String position,

    Integer imp,
    String award,

    // Support stats
    Integer campStack,
    Integer courierKills,
    Integer sentryWardsPurchased,
    Integer observerWardsPurchased,
    Integer sentryWardsDestroyed,
    Integer observerWardsDestroyed,

    // Audit fields
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
} 
