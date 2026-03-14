package com.batrobot.binding.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.batrobot.binding.infrastructure.persistence.entity.ChatPlayerBindingEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository for ChatPlayerBinding entity (JPA persistence layer).
 */
@Repository
public interface ChatPlayerBindingJpaRepository extends JpaRepository<ChatPlayerBindingEntity, UUID> {
    
    /**
     * Finds a binding by chat, user, and steam account IDs.
     * Uses shorter method name with @Query for better readability.
     */
    @Query("SELECT cpb FROM ChatPlayerBindingEntity cpb " +
           "WHERE cpb.telegramChatId = :chatId " +
           "AND cpb.telegramUserId = :userId " +
           "AND cpb.steamId = :steamId")
    Optional<ChatPlayerBindingEntity> findBinding(
            @Param("chatId") Long chatId,
            @Param("userId") Long userId,
            @Param("steamId") Long steamId);

    /**
     * Finds a binding by chat and steam account IDs.
     */
    @Query("SELECT cpb FROM ChatPlayerBindingEntity cpb " +
          "WHERE cpb.telegramChatId = :chatId " +
          "AND cpb.steamId = :steamId")
    Optional<ChatPlayerBindingEntity> findBindingInChatBySteamId(
           @Param("chatId") Long chatId,
           @Param("steamId") Long steamId);
    
    /**
     * Checks if a binding exists without loading the entity.
     * More efficient than findBinding(...).isPresent().
     */
    @Query("SELECT CASE WHEN COUNT(cpb) > 0 THEN true ELSE false END " +
           "FROM ChatPlayerBindingEntity cpb " +
           "WHERE cpb.telegramChatId = :chatId " +
           "AND cpb.telegramUserId = :userId " +
           "AND cpb.steamId = :steamId")
    boolean existsBinding(
            @Param("chatId") Long chatId,
            @Param("userId") Long userId,
            @Param("steamId") Long steamId);
    
    /**
     * Finds bindings by chat and user.
     */
    @Query("SELECT cpb FROM ChatPlayerBindingEntity cpb " +
           "WHERE cpb.telegramChatId = :chatId " +
           "AND cpb.telegramUserId = :userId")
    List<ChatPlayerBindingEntity> findUserBindingsInChat(
            @Param("chatId") Long chatId,
            @Param("userId") Long userId);
    
    /**
     * Finds bindings for a specific chat.
     */
    @Query("SELECT cpb FROM ChatPlayerBindingEntity cpb WHERE cpb.telegramChatId = :chatId")
    List<ChatPlayerBindingEntity> findBindingsInChat(@Param("chatId") Long chatId);
    
    /**
     * Finds bindings for a specific Steam account.
     */
    @Query("SELECT cpb FROM ChatPlayerBindingEntity cpb WHERE cpb.steamId = :steamId")
    List<ChatPlayerBindingEntity> findBindingsForSteamAccount(@Param("steamId") Long steamId);
    
    /**
     * Finds the primary binding for a user in a chat.
     */
    @Query("SELECT cpb FROM ChatPlayerBindingEntity cpb " +
           "WHERE cpb.telegramChatId = :chatId " +
           "AND cpb.telegramUserId = :userId " +
           "AND cpb.isPrimary = true")
    Optional<ChatPlayerBindingEntity> findPrimaryBinding(
            @Param("chatId") Long chatId,
            @Param("userId") Long userId);
}

