package com.batrobot.user.infrastructure.persistence.repository;

import com.batrobot.user.domain.model.User;
import com.batrobot.user.domain.repository.UserRepository;
import com.batrobot.user.infrastructure.persistence.entity.UserEntity;
import com.batrobot.user.infrastructure.persistence.mapper.UserEntityMapper;
import com.batrobot.shared.domain.model.valueobject.TelegramUserId;
import com.batrobot.shared.infrastructure.persistence.repository.RepositoryAdapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter implementation of TelegramUserRepository (domain interface).
 */
@Repository
@RequiredArgsConstructor
public class UserJpaRepositoryAdapter extends RepositoryAdapter<User, UserEntity, UUID>
        implements UserRepository {
    
    private final UserJpaRepository jpaRepository; // JPA repository
    private final UserEntityMapper mapper;
    
    @Override
    public Optional<User> findById(UUID userId) {
        return jpaRepository.findById(userId)
            .map(mapper::toDomain);
    }
    
    @Override
    public List<User> findAllById(Collection<UUID> userIds) {
        return jpaRepository.findAllById(userIds)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public User save(User user) {
        return saveAggregate(user);
    }
    
    @Override
    public boolean existsById(UUID userId) {
        return jpaRepository.existsById(userId);
    }
    
    @Override
    public void deleteById(UUID userId) {
        validateExists(userId);
        jpaRepository.deleteById(userId);
    }
    
    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public Optional<User> findByTelegramUserId(TelegramUserId telegramUserId) {
        return jpaRepository.findByTelegramUserId(telegramUserId.value())
            .map(mapper::toDomain);
    }

    @Override
    public boolean existsByTelegramUserId(TelegramUserId telegramUserId) {
        return jpaRepository.existsByTelegramUserId(telegramUserId.value());
    }

    @Override
    public List<User> findAllByTelegramUserId(Collection<TelegramUserId> telegramUserIds) {
        List<Long> primitiveIds = telegramUserIds.stream()
            .map(TelegramUserId::value)
            .collect(Collectors.toList());
        
        return jpaRepository.findAllByTelegramUserIdIn(primitiveIds)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    protected User toDomain(UserEntity entity) {
        return mapper.toDomain(entity);
    }

    @Override
    protected UserEntity toEntity(User aggregate) {
        return mapper.toEntity(aggregate);
    }

    @Override
    protected UserEntity persistEntity(UserEntity entity) {
        return jpaRepository.save(entity);
    }
}


