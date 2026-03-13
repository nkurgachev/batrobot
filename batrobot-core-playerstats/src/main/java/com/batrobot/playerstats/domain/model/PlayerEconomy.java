package com.batrobot.playerstats.domain.model;

import com.batrobot.playerstats.domain.exception.InvalidPlayerEconomyException;

/**
 * Value Object representing player economy in a match.
 */
public record PlayerEconomy(
        Integer numLastHits,
        Integer numDenies,
        Integer goldPerMinute,
        Integer experiencePerMinute
) {
    
    public PlayerEconomy {
        // Validate non-negative
        if ((numLastHits != null && numLastHits < 0) || 
            (numDenies != null && numDenies < 0) ||
            (goldPerMinute != null && goldPerMinute < 0) ||
            (experiencePerMinute != null && experiencePerMinute < 0)) {
            throw new InvalidPlayerEconomyException("Economy values cannot be negative");
        }
    }
    
    /**
     * Calculates CS (Creep Score) = Last Hits + Denies.
     */
    public int getCreepScore() {
        return (numLastHits != null ? numLastHits : 0) + (numDenies != null ? numDenies : 0);
    }
}

