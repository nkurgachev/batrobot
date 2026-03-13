package com.batrobot.match.infrastructure.persistence.repository;

import com.batrobot.match.domain.model.Match;
import com.batrobot.match.domain.repository.MatchRepository;
import com.batrobot.match.infrastructure.persistence.entity.MatchEntity;
import com.batrobot.match.infrastructure.persistence.mapper.MatchEntityMapper;
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
 * Adapter implementation of MatchRepository (domain interface).
 * Extends RepositoryAdapter for common CRUD functionality.
 */
@Repository
@RequiredArgsConstructor
public class MatchJpaRepositoryAdapter extends RepositoryAdapter<Match, MatchEntity, UUID>
        implements MatchRepository {
    
    private final MatchJpaRepository jpaRepository;
    private final MatchEntityMapper mapper;
    
    @Override
    public Optional<Match> findById(UUID uuid) {
        return jpaRepository.findById(uuid)
            .map(mapper::toDomain);
    }
    
    @Override
    public Match save(Match match) {
        return saveAggregate(match);
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
    public List<Match> findAllById(Collection<UUID> ids) {
        return jpaRepository.findAllById(ids).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    /**
     * Find Match by external Stratz API match ID (business key).
     * Used when processing external match data where we have the matchId but not the UUID.
     */
    public Optional<Match> findByMatchId(MatchId matchId) {
        return jpaRepository.findByMatchId(matchId.value())
            .map(mapper::toDomain);
    }

    @Override
    public List<Match> findRecentMatches(long startTime) {
        return jpaRepository.findRecentMatches(startTime).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    protected Match toDomain(MatchEntity entity) {
        return mapper.toDomain(entity);
    }

    @Override
    protected MatchEntity toEntity(Match aggregate) {
        return mapper.toEntity(aggregate);
    }

    @Override
    protected MatchEntity persistEntity(MatchEntity entity) {
        return jpaRepository.save(entity);
    }
}

