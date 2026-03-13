package com.batrobot.user.domain.repository;

import com.batrobot.user.domain.model.User;
import com.batrobot.shared.domain.model.valueobject.TelegramUserId;
import com.batrobot.shared.domain.repository.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for User aggregate.
 */
public interface UserRepository extends Repository<User, UUID> {
    
    /**
     * Finds a Telegram user by their external Telegram user ID.
     * 
     * @param telegramUserId External Telegram user ID
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByTelegramUserId(TelegramUserId telegramUserId);
    
    /**
     * Checks if user with given Telegram user ID exists.
     * 
     * @param telegramUserId External Telegram user ID
     * @return true if user exists, false otherwise
     */
    boolean existsByTelegramUserId(TelegramUserId telegramUserId);

    /**
     * Finds multiple Telegram users by their external Telegram user IDs.
     * Useful for batch operations and aggregates that reference users by TelegramUserId.
     * 
     * @param telegramUserIds Collection of external Telegram user IDs
     * @return List of users found
     */
    List<User> findAllByTelegramUserId(Collection<TelegramUserId> telegramUserIds);
}

