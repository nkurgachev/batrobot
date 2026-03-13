package com.batrobot.match.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.batrobot.match.infrastructure.persistence.entity.MatchEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository for Match entity (JPA persistence layer).
 */
@Repository
public interface MatchJpaRepository extends JpaRepository<MatchEntity, UUID> {
    
    /**
     * Find match by external Stratz API match ID.
     */
    Optional<MatchEntity> findByMatchId(Long matchId);
    
    @Query("SELECT m FROM MatchEntity m WHERE m.startDateTime >= :startTime " +
           "ORDER BY m.startDateTime DESC")
    List<MatchEntity> findRecentMatches(@Param("startTime") Long startTime);
    
    @Query("DELETE FROM MatchEntity m WHERE m.startDateTime < :cutoffTime")
    void deleteOldMatches(@Param("cutoffTime") Long cutoffTime);
}


