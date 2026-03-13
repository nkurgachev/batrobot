package com.batrobot.shared.domain.model.valueobject;

import com.batrobot.shared.domain.exception.valueobject.InvalidPlayerRankException;

/**
 * Value Object representing Dota 2 season rank (MMR medal).
 */
public record SeasonRank(Integer value) {
    
    private static final int MIN_RANK = 0;   // Unranked
    private static final int MAX_RANK = 80;  // Immortal max
    
    public SeasonRank {
        if (value != null && (value < MIN_RANK || value > MAX_RANK)) {
            throw new InvalidPlayerRankException(
                String.format("Season rank must be between %d and %d, got: %d", 
                    MIN_RANK, MAX_RANK, value)
            );
        }
    }
    
    public static SeasonRank of(Integer value) {
        return new SeasonRank(value);
    }
    
    public static SeasonRank unranked() {
        return new SeasonRank(null);
    }
    
    public boolean isRanked() {
        return value != null && value > 0;
    }
    
    public boolean isUnranked() {
        return !isRanked();
    }
    
    /**
     * Returns medal tier (Herald=1, Guardian=2, Crusader=3, etc).
     */
    public Integer getMedalTier() {
        if (value == null || value == 0) {
            return null;
        }
        return value / 10;
    }
    
    /**
     * Returns star level within medal (1-5).
     */
    public Integer getStarLevel() {
        if (value == null || value == 0) {
            return null;
        }
        return value % 10;
    }
}
