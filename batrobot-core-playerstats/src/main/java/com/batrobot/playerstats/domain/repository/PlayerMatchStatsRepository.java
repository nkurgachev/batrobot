package com.batrobot.playerstats.domain.repository;

import com.batrobot.playerstats.domain.model.PlayerMatchStats;
import com.batrobot.shared.domain.model.valueobject.MatchId;
import com.batrobot.shared.domain.model.valueobject.SteamId;
import com.batrobot.shared.domain.repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain Repository for PlayerMatchStats.
 */
public interface PlayerMatchStatsRepository extends Repository<PlayerMatchStats, UUID> {
    
    /**
     * Finds player stats by match and player.
     */
    Optional<PlayerMatchStats> findByMatchIdAndSteamId(MatchId matchId, SteamId steamId);
    
    /**
     * Finds all stats for a specific match.
     */
    List<PlayerMatchStats> findByMatchId(MatchId matchId);
    
    /**
     * Finds all stats for a specific player.
     */
    List<PlayerMatchStats> findBySteamId(SteamId steamId);

    /**
     * Finds all stats for multiple players.
     */
    List<PlayerMatchStats> findStatsForPlayers(List<SteamId> steamIds);

    /**
     * Finds stats for specified players in specified matches.
     */
    List<PlayerMatchStats> findByMatchIdsAndSteamIds(List<MatchId> matchIds, List<SteamId> steamIds);

    /**
     * Finds latest stats for a player (by match end/start time).
     */
    Optional<PlayerMatchStats> findLatestStatsBySteamId(SteamId steamId);
}

