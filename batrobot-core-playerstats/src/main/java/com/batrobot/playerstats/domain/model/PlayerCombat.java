package com.batrobot.playerstats.domain.model;

import com.batrobot.playerstats.domain.exception.InvalidPlayerCombatException;

/**
 * Value Object representing player combat metrics in a match.
 */
public record PlayerCombat(
        Integer heroDamage,
        Integer towerDamage,
        Integer heroHealing
) {
    
    public PlayerCombat {
        // Validate non-negative
        if ((heroDamage != null && heroDamage < 0) || 
            (towerDamage != null && towerDamage < 0) ||
            (heroHealing != null && heroHealing < 0)) {
            throw new InvalidPlayerCombatException("Combat values cannot be negative");
        }
    }
    
    /**
     * Calculates total damage dealt.
     */
    public long getTotalDamage() {
        return (heroDamage != null ? heroDamage : 0) + (towerDamage != null ? towerDamage : 0);
    }
}

