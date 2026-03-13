package com.batrobot.match.domain.repository;

import com.batrobot.match.domain.model.Match;
import com.batrobot.shared.domain.model.valueobject.MatchId;
import com.batrobot.shared.domain.repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Match aggregate.
 */
public interface MatchRepository extends Repository<Match, UUID> {
    
    /**
     * Find Match by external Stratz API match ID (business key).
     * Used when processing external match data where we have the matchId but not the UUID.
     */
    Optional<Match> findByMatchId(MatchId matchId);

    /**
     * Finds matches that started at or after the given timestamp.
     */
    List<Match> findRecentMatches(long startTime);
}

