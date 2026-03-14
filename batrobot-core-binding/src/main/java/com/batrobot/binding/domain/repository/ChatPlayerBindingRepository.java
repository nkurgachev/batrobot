package com.batrobot.binding.domain.repository;

import com.batrobot.binding.domain.model.ChatPlayerBinding;
import com.batrobot.shared.domain.model.valueobject.SteamId;
import com.batrobot.shared.domain.model.valueobject.TelegramChatId;
import com.batrobot.shared.domain.model.valueobject.TelegramUserId;
import com.batrobot.shared.domain.repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain Repository for ChatPlayerBinding
 */
public interface ChatPlayerBindingRepository extends Repository<ChatPlayerBinding, UUID> {
    
    /**
     * Finds binding for a specific chat, user, and player.
     */
    Optional<ChatPlayerBinding> findBindingForUser(
            TelegramChatId chatId,
            TelegramUserId userId,
            SteamId steamId
    );

    /**
     * Finds a binding for a specific Steam account in a specific chat.
     */
    Optional<ChatPlayerBinding> findBindingInChatBySteamId(
            TelegramChatId chatId,
            SteamId steamId
    );
    
    /**
     * Finds all bindings for a specific user in a chat.
     */
    List<ChatPlayerBinding> findBindingsForUserInChat(
            TelegramChatId chatId,
            TelegramUserId userId
    );
    
    /**
     * Finds all bindings in a specific chat.
     */
    List<ChatPlayerBinding> findBindingsInChat(TelegramChatId chatId);
    
    /**
     * Finds all bindings for a specific player.
     */
    List<ChatPlayerBinding> findBindingsForSteamAccount(SteamId steamId);
    
    /**
     * Finds primary binding for a user in a chat.
     */
    Optional<ChatPlayerBinding> findPrimaryBindingForUserInChat(
            TelegramChatId chatId,
            TelegramUserId userId
    );
}

