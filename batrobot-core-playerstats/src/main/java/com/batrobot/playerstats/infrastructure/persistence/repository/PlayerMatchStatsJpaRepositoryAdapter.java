package com.batrobot.playerstats.infrastructure.persistence.repository;

import com.batrobot.playerstats.domain.model.PlayerMatchStats;
import com.batrobot.playerstats.domain.repository.PlayerMatchStatsRepository;
import com.batrobot.playerstats.infrastructure.persistence.entity.PlayerMatchStatsEntity;
import com.batrobot.playerstats.infrastructure.persistence.mapper.PlayerMatchStatsEntityMapper;
import com.batrobot.shared.domain.model.valueobject.*;
import com.batrobot.shared.infrastructure.persistence.repository.RepositoryAdapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter: Implements domain PlayerMatchStatsRepository using JPA layer.
 * 
 * Bridges the domain repository interface with Spring Data JPA.
 * Low-level persistence details (queries, JPA entities) are hidden from domain layer.
 * Extends RepositoryAdapter for common CRUD functionality.
 * 
 * Note: PlayerMatchStats entity stores steamId and matchId as denormalized columns,
 * enabling independent microservice separation (no JPA relationships to other entities).
 */
@Repository
@RequiredArgsConstructor
public class PlayerMatchStatsJpaRepositoryAdapter extends RepositoryAdapter<PlayerMatchStats, PlayerMatchStatsEntity, UUID>
        implements PlayerMatchStatsRepository {
    
    private final PlayerMatchStatsJpaRepository jpaRepository;
    private final PlayerMatchStatsEntityMapper mapper;
    
    @Override
    public Optional<PlayerMatchStats> findByMatchIdAndSteamId(MatchId matchId, SteamId steamId) {
        return jpaRepository.findBySteamIdAndMatchId(steamId.value(), matchId.value())
                .map(mapper::toDomain);
    }
    
    @Override
    public List<PlayerMatchStats> findByMatchId(MatchId matchId) {
        return jpaRepository.findByMatchId(matchId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public List<PlayerMatchStats> findBySteamId(SteamId steamId) {
        return jpaRepository.findBySteamId(steamId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<PlayerMatchStats> findStatsForPlayers(List<SteamId> steamIds) {
        List<Long> ids = steamIds.stream()
            .map(SteamId::value)
            .toList();

        return jpaRepository.findStatsForPlayers(ids)
            .stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public Optional<PlayerMatchStats> findLatestStatsBySteamId(SteamId steamId) {
        return jpaRepository.findLatestStatsBySteamId(steamId.value())
            .map(mapper::toDomain);
    }

    @Override
    public List<PlayerMatchStats> findByMatchIdsAndSteamIds(List<MatchId> matchIds, List<SteamId> steamIds) {
        List<Long> matchIdValues = matchIds.stream().map(MatchId::value).toList();
        List<Long> steamIdValues = steamIds.stream().map(SteamId::value).toList();

        return jpaRepository.findByMatchIdsAndSteamIds(matchIdValues, steamIdValues)
            .stream()
            .map(mapper::toDomain)
            .toList();
    }
    
    @Override
    public PlayerMatchStats save(PlayerMatchStats stats) {
        PlayerMatchStatsEntity entity = mapper.toEntity(stats);
        PlayerMatchStatsEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<PlayerMatchStats> findById(UUID uuid) {
        return jpaRepository.findById(uuid)
                .map(mapper::toDomain);
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
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public List<PlayerMatchStats> findAllById(Collection<UUID> ids) {
        return jpaRepository.findAllById(ids).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    protected PlayerMatchStats toDomain(PlayerMatchStatsEntity entity) {
        return mapper.toDomain(entity);
    }

    @Override
    protected PlayerMatchStatsEntity toEntity(PlayerMatchStats aggregate) {
        return mapper.toEntity(aggregate);
    }

    @Override
    protected PlayerMatchStatsEntity persistEntity(PlayerMatchStatsEntity entity) {
        return jpaRepository.save(entity);
    }
}

