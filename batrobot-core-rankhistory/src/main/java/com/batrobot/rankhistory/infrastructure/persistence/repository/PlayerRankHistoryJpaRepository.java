package com.batrobot.rankhistory.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.batrobot.rankhistory.infrastructure.persistence.entity.PlayerRankHistoryEntity;

import java.util.List;
import java.util.UUID;

/**
 * JPA Repository for PlayerRankHistory entity (JPA persistence layer).
 */
@Repository
public interface PlayerRankHistoryJpaRepository extends JpaRepository<PlayerRankHistoryEntity, UUID> {

    List<PlayerRankHistoryEntity> findAllBySteamIdOrderByAssignedAtAsc(Long steamId);

    List<PlayerRankHistoryEntity> findAllBySteamIdInOrderBySteamIdAscAssignedAtAsc(List<Long> steamIds);
}


