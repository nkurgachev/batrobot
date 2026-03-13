package com.batrobot.playerstats.application.dto.request;

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
public class PlayerMatchStatsRequest {
    @NotNull(message = "Match ID cannot be null")
    private Long matchId;
    @NotNull(message = "Steam ID cannot be null")
    private Long steamId64;

    // Hero info
    @NotNull(message = "Hero ID cannot be null")
    private Integer heroId;
    private String heroName;

    // Game outcome
    @NotNull(message = "Victory status cannot be null")
    private Boolean isVictory;
    @NotNull(message = "Radiant status cannot be null")
    private Boolean isRadiant;

    // Performance stats
    private Integer kills;
    private Integer deaths;
    private Integer assists;

    private Integer numLastHits;
    private Integer numDenies;
    private Integer goldPerMinute;
    private Integer experiencePerMinute;

    private Integer heroDamage;
    private Integer towerDamage;
    private Integer heroHealing;

    // Position info
    private String lane;
    private String position;

    private Integer imp;
    private String award;

    // Support stats
    private Integer campStack;
    private Integer courierKills;
    private Integer sentryWardsPurchased;
    private Integer observerWardsPurchased;
    private Integer sentryWardsDestroyed;
    private Integer observerWardsDestroyed;
}
