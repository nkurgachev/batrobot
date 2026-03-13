package com.batrobot.playerstats.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.batrobot.playerstats.infrastructure.persistence.entity.PlayerMatchStatsEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository for PlayerMatchStats entity (JPA persistence layer).
 * 
 * NOTE: This is NOT the domain repository - see PlayerMatchStatsRepository in domain layer.
 * This interface is used internally by PlayerMatchStatsJpaRepositoryAdapter.
 * 
 * Primary key is UUID; (steamId, matchId) is a business key unique constraint.
 * Note: match_id is a denormalized foreign key storing Stratz API ID for future microservice separation.
 */
@Repository
public interface PlayerMatchStatsJpaRepository extends JpaRepository<PlayerMatchStatsEntity, UUID> {
    
    @Query("SELECT pms FROM PlayerMatchStatsEntity pms " +
           "WHERE pms.steamId = :steamId " +
           "ORDER BY pms.createdAt DESC")
    List<PlayerMatchStatsEntity> findRecentStatsBySteamId(@Param("steamId") Long steamId);
    
    @Query("SELECT DISTINCT pms FROM PlayerMatchStatsEntity pms " +
           "WHERE pms.steamId IN :steamIds " +
           "ORDER BY pms.steamId, pms.createdAt DESC")
    List<PlayerMatchStatsEntity> findStatsForPlayers(@Param("steamIds") List<Long> steamIds);
    
    @Query("SELECT pms FROM PlayerMatchStatsEntity pms " +
           "WHERE pms.steamId = :steamId " +
           "AND pms.matchId = :matchId")
    Optional<PlayerMatchStatsEntity> findBySteamIdAndMatchId(@Param("steamId") Long steamId,
                                                        @Param("matchId") Long matchId);
    
    @Query("SELECT pms FROM PlayerMatchStatsEntity pms " +
           "WHERE pms.steamId = :steamId " +
           "ORDER BY pms.createdAt DESC " +
           "LIMIT 1")
    Optional<PlayerMatchStatsEntity> findLatestStatsBySteamId(@Param("steamId") Long steamId);

    @Query("SELECT pms FROM PlayerMatchStatsEntity pms " +
           "WHERE pms.matchId = :matchId")
    List<PlayerMatchStatsEntity> findByMatchId(@Param("matchId") Long matchId);
    
    @Query("SELECT pms FROM PlayerMatchStatsEntity pms " +
           "WHERE pms.steamId = :steamId")
    List<PlayerMatchStatsEntity> findBySteamId(@Param("steamId") Long steamId);

    @Query("SELECT DISTINCT pms FROM PlayerMatchStatsEntity pms " +
           "WHERE pms.matchId IN :matchIds " +
           "AND pms.steamId IN :steamIds " +
           "ORDER BY pms.steamId, pms.createdAt DESC")
    List<PlayerMatchStatsEntity> findByMatchIdsAndSteamIds(@Param("matchIds") List<Long> matchIds,
                                                           @Param("steamIds") List<Long> steamIds);
}




