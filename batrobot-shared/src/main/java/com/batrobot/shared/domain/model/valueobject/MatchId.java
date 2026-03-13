package com.batrobot.shared.domain.model.valueobject;

import com.batrobot.shared.domain.exception.valueobject.InvalidMatchIdException;

/**
 * Value Object representing Dota 2 Match ID from external source (Stratz/Steam).
 */
public record MatchId(Long value) {
    public static final long MIN_MATCH_ID = 1;

    public MatchId {
        if (value == null) {
            throw new InvalidMatchIdException("Match ID cannot be null");
        }
        if (value < MIN_MATCH_ID) {
            throw new InvalidMatchIdException(
                String.format("Match ID must be >= %d, got %d", MIN_MATCH_ID, value)
            );
        }
    }

    /**
     * Factory method to create MatchId.
     * @param value Match ID
     * @return MatchId instance
     * @throws InvalidMatchIdException if ID is invalid
     */
    public static MatchId of(Long value) {
        return new MatchId(value);
    }
}
