package com.batrobot.chat.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.batrobot.chat.infrastructure.persistence.entity.ChatEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository for TelegramChat entity (JPA persistence layer).
 */
@Repository
public interface ChatJpaRepository extends JpaRepository<ChatEntity, UUID> {

    /**
     * Find a chat by their external Telegram ID.
     * 
     * @param telegramChatId The external Telegram chat ID
     * @return Optional containing the chat if found
     */
    Optional<ChatEntity> findByTelegramChatId(Long telegramChatId);

    /**
     * Check if a chat exists by their external Telegram ID.
     * 
     * @param telegramChatId The external Telegram chat ID
     * @return true if chat exists, false otherwise
     */
    boolean existsByTelegramChatId(Long telegramChatId);

    /**
     * Find multiple chats by their external Telegram IDs.
     * 
     * @param telegramChatIds Collection of external Telegram chat IDs
     * @return List of chats found
     */
    List<ChatEntity> findAllByTelegramChatIdIn(Collection<Long> telegramChatIds);
}

