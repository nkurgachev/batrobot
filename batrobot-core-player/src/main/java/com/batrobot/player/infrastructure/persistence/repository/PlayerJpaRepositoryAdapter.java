package com.batrobot.player.infrastructure.persistence.repository;

import com.batrobot.player.domain.model.Player;
import com.batrobot.player.domain.repository.PlayerRepository;
import com.batrobot.player.infrastructure.persistence.entity.PlayerEntity;
import com.batrobot.player.infrastructure.persistence.mapper.PlayerEntityMapper;
import com.batrobot.shared.domain.model.valueobject.SteamId;
import com.batrobot.shared.infrastructure.persistence.repository.RepositoryAdapter;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter implementation of PlayerRepository (domain interface).
 */
@Repository
@RequiredArgsConstructor
public class PlayerJpaRepositoryAdapter extends RepositoryAdapter<Player, PlayerEntity, UUID>
        implements PlayerRepository {
    
    private final PlayerJpaRepository jpaRepository;
    private final PlayerEntityMapper mapper;
    private final EntityManager entityManager;
    
    @Override
    public Optional<Player> findBySteamId(SteamId steamId) {
        return jpaRepository.findBySteamId(steamId.value())
            .map(mapper::toDomain);
    }
    
    @Override
    public boolean existsBySteamId(SteamId steamId) {
        return jpaRepository.existsBySteamId(steamId.value());
    }
    
    @Override
    public Player save(Player account) {
        return saveAggregate(account);
    }
    
    @Override
    public List<Player> findAllBySteamIds(List<SteamId> steamIds) {
        List<Long> primitiveIds = steamIds.stream()
            .map(SteamId::value)
            .collect(Collectors.toList());
        
        return jpaRepository.findAllBySteamIdIn(primitiveIds)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Player> findAllByOrderByUpdatedAt() {
        return jpaRepository.findAllByOrderByUpdatedAtAsc()
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Player> findAllByOrderByUpdatedAtDesc() {
        return jpaRepository.findAllByOrderByUpdatedAtDesc()
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Player> findAllById(java.util.Collection<UUID> ids) {
        return jpaRepository.findAllById(ids)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Player> findById(UUID id) {
        return jpaRepository.findById(id)
            .map(mapper::toDomain);
    }
    
    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
    
    @Override
    public void deleteById(UUID id) {
        validateExists(id);
        jpaRepository.deleteById(id);
    }
    
    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    protected Player toDomain(PlayerEntity entity) {
        return mapper.toDomain(entity);
    }

    @Override
    protected PlayerEntity toEntity(Player aggregate) {
        return mapper.toEntity(aggregate);
    }

    @Override
    protected PlayerEntity persistEntity(PlayerEntity entity) {
        if (entity.getId() != null && !jpaRepository.existsById(entity.getId())) {
            entityManager.persist(entity);
            return entity;
        }
        return jpaRepository.save(entity);
    }
}

