package com.batrobot.user.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.batrobot.user.infrastructure.persistence.entity.UserEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository for User entity (JPA persistence layer).
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {

    /**
     * Find a user by their external Telegram ID.
     * 
     * @param telegramUserId The external Telegram user ID
     * @return Optional containing the user if found
     */
    Optional<UserEntity> findByTelegramUserId(Long telegramUserId);

    /**
     * Check if a user exists by their external Telegram ID.
     * 
     * @param telegramUserId The external Telegram user ID
     * @return true if user exists, false otherwise
     */
    boolean existsByTelegramUserId(Long telegramUserId);

    /**
     * Find multiple users by their external Telegram IDs.
     * 
     * @param telegramUserIds Collection of external Telegram user IDs
     * @return List of users found
     */
    List<UserEntity> findAllByTelegramUserIdIn(Collection<Long> telegramUserIds);
}





