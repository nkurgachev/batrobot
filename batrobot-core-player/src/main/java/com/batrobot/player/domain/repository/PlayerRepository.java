package com.batrobot.player.domain.repository;

import com.batrobot.player.domain.model.Player;
import com.batrobot.shared.domain.model.valueobject.SteamId;
import com.batrobot.shared.domain.repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Player aggregate.
 */
public interface PlayerRepository extends Repository<Player, UUID> {
    
    /**
     * Finds a player by their Steam ID (business key).
     * 
     * @param steamId Steam ID value object (unique business key)
     * @return Optional containing the player if found, empty otherwise
     */
    Optional<Player> findBySteamId(SteamId steamId);
    
    /**
     * Checks if player with given Steam ID exists.
     * 
     * @param steamId Steam ID value object (unique business key)
     * @return true if player exists, false otherwise
     */
    boolean existsBySteamId(SteamId steamId);
    
    /**
     * Finds all players by their Steam IDs.
     * 
     * @param steamIds List of Steam ID value objects
     * @return List of players found (may be less than requested if some don't exist)
     */
    List<Player> findAllBySteamIds(List<SteamId> steamIds);
    
    /**
     * Finds all players ordered by last update time (ascending).
     * Used for scheduled updates.
     * 
     * @return List of players sorted by updatedAt ASC
     */
    List<Player> findAllByOrderByUpdatedAt();
    
    /**
     * Finds all players ordered by last update time (descending).
     * Used for displaying recently updated players.
     * 
     * @return List of players sorted by updatedAt DESC
     */
    List<Player> findAllByOrderByUpdatedAtDesc();
}

