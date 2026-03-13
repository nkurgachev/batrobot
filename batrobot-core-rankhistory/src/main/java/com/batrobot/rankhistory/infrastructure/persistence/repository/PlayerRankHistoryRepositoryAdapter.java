package com.batrobot.rankhistory.infrastructure.persistence.repository;

import com.batrobot.rankhistory.domain.model.PlayerRankHistory;
import com.batrobot.rankhistory.domain.repository.PlayerRankHistoryRepository;
import com.batrobot.rankhistory.infrastructure.persistence.entity.PlayerRankHistoryEntity;
import com.batrobot.rankhistory.infrastructure.persistence.mapper.PlayerRankHistoryEntityMapper;
import com.batrobot.shared.domain.model.valueobject.*;
import com.batrobot.shared.infrastructure.persistence.repository.RepositoryAdapter;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter implementation of PlayerRankHistoryRepository (domain interface).
 */
@Repository
@RequiredArgsConstructor
public class PlayerRankHistoryRepositoryAdapter extends RepositoryAdapter<PlayerRankHistory, PlayerRankHistoryEntity, UUID>
    implements PlayerRankHistoryRepository {

    private final PlayerRankHistoryJpaRepository jpaRepository;
    private final PlayerRankHistoryEntityMapper mapper;

    @Override
    public List<PlayerRankHistory> findAllBySteamIdInOrderBySteamIdAscAssignedAtAsc(List<SteamId> steamIds) {
        List<Long> ids = steamIds.stream()
            .map(SteamId::value)
            .toList();
        return jpaRepository.findAllBySteamIdInOrderBySteamIdAscAssignedAtAsc(ids)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<PlayerRankHistory> findAllBySteamIdOrderByAssignedAtAsc(SteamId steamId) {
        return jpaRepository.findAllBySteamIdOrderByAssignedAtAsc(steamId.value())
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<PlayerRankHistory> findById(UUID uuid) {
        return jpaRepository.findById(uuid)
            .map(mapper::toDomain);
    }

    @Override
    public PlayerRankHistory save(PlayerRankHistory history) {
        return saveAggregate(history);
    }

    @Override
    public boolean existsById(UUID uuid) {
        return jpaRepository.existsById(uuid);
    }

    @Override
    public void deleteById(UUID uuid) {
        validateExists(uuid);
        jpaRepository.deleteById(uuid);
    }

    @Override
    protected PlayerRankHistory toDomain(PlayerRankHistoryEntity entity) {
        return mapper.toDomain(entity);
    }

    @Override
    protected PlayerRankHistoryEntity toEntity(PlayerRankHistory aggregate) {
        return mapper.toEntity(aggregate);
    }

    @Override
    protected PlayerRankHistoryEntity persistEntity(PlayerRankHistoryEntity entity) {
        return jpaRepository.save(entity);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public List<PlayerRankHistory> findAllById(Collection<UUID> ids) {
        return jpaRepository.findAllById(ids).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
}

