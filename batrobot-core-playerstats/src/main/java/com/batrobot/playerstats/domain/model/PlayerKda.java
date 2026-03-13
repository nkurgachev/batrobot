package com.batrobot.playerstats.domain.model;

import com.batrobot.playerstats.domain.exception.InvalidPlayerKdaException;

/**
 * Value Object representing player statistics in a match.
 */
public record PlayerKda(
        Integer kills,
        Integer deaths,
        Integer assists
) {
    
    public PlayerKda {
        // Allow nulls, but validate non-negative when present
        if ((kills != null && kills < 0) || (deaths != null && deaths < 0) || (assists != null && assists < 0)) {
            throw new InvalidPlayerKdaException("KDA values cannot be negative");
        }
    }
    
    /**
     * Calculates KDA ratio (kills + assists) / (max(1, deaths)).
     */
    public double getKdaRatio() {
        int totalOffensive = (kills != null ? kills : 0) + (assists != null ? assists : 0);
        int deathCount = Math.max(1, deaths != null ? deaths : 0);
        return (double) totalOffensive / deathCount;
    }
}

