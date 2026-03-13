package com.batrobot.stratz.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Intermediate DTO representing player stats within a match from Stratz API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StratzPlayerMatchStatsResponse {
    private Integer heroId;
    private String heroName;
    private Boolean isVictory;
    private Boolean isRadiant;
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
    private String lane;
    private String position;
    private Integer imp;
    private String award;
    private Integer campStack;
    private Integer courierKills;
    private Integer sentryWardsPurchased;
    private Integer observerWardsPurchased;
    private Integer sentryWardsDestroyed;
    private Integer observerWardsDestroyed;
}

