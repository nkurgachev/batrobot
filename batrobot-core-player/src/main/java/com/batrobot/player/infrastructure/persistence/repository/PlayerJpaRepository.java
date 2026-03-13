package com.batrobot.player.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.batrobot.player.infrastructure.persistence.entity.PlayerEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository for Player entity (JPA persistence layer).
 */
public interface PlayerJpaRepository extends JpaRepository<PlayerEntity, UUID> {
    
    /**
     * Finds a player by their Steam ID (business key).
     */
    Optional<PlayerEntity> findBySteamId(Long steamId);
    
    /**
     * Checks if a player with given Steam ID exists.
     */
    boolean existsBySteamId(Long steamId);
    
    /**
     * Finds all players by their Steam IDs.
     */
    List<PlayerEntity> findAllBySteamIdIn(List<Long> steamIds);
    
    /**
     * Finds all players ordered by last update time (ascending).
     * Used for scheduled batch updates.
     */
    List<PlayerEntity> findAllByOrderByUpdatedAtAsc();
    
    /**
     * Finds all players ordered by last update time (descending).
     * Used for displaying recently updated players.
     */
    List<PlayerEntity> findAllByOrderByUpdatedAtDesc();
}

