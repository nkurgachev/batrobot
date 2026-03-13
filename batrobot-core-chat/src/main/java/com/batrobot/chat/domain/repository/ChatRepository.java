package com.batrobot.chat.domain.repository;

import com.batrobot.chat.domain.model.Chat;
import com.batrobot.shared.domain.model.valueobject.TelegramChatId;
import com.batrobot.shared.domain.repository.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Chat aggregate.
 */
public interface ChatRepository extends Repository<Chat, UUID> {
    
    /**
     * Finds a Telegram chat by their external Telegram chat ID.
     * 
     * @param telegramChatId External Telegram chat ID
     * @return Optional containing the chat if found, empty otherwise
     */
    Optional<Chat> findByTelegramChatId(TelegramChatId telegramChatId);
    
    /**
     * Checks if chat with given Telegram chat ID exists.
     * 
     * @param telegramChatId External Telegram chat ID
     * @return true if chat exists, false otherwise
     */
    boolean existsByTelegramChatId(TelegramChatId telegramChatId);

    /**
     * Finds multiple Telegram chats by their external Telegram chat IDs.
     * Useful for batch operations and aggregates that reference chats by TelegramChatId.
     * 
     * @param telegramChatIds Collection of external Telegram chat IDs
     * @return List of chats found
     */
    List<Chat> findAllByTelegramChatId(Collection<TelegramChatId> telegramChatIds);
}


