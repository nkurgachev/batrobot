package com.batrobot.rankhistory.domain.repository;

import com.batrobot.rankhistory.domain.model.PlayerRankHistory;
import com.batrobot.shared.domain.model.valueobject.SteamId;
import com.batrobot.shared.domain.repository.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Domain Repository for PlayerRankHistory.
 */
public interface PlayerRankHistoryRepository extends Repository<PlayerRankHistory, UUID> {

    List<PlayerRankHistory> findAllBySteamIdInOrderBySteamIdAscAssignedAtAsc(List<SteamId> steamIds);
    
    /**
     * Finds all rank history records for a single Steam account, ordered by assignment date.
     * 
     * @param steamId Steam account identifier
     * @return List of rank history records ordered by assignedAt ascending
     */
    List<PlayerRankHistory> findAllBySteamIdOrderByAssignedAtAsc(SteamId steamId);
}

