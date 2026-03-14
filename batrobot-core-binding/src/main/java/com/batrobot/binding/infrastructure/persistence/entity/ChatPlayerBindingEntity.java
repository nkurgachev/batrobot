package com.batrobot.binding.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "chat_player_bindings",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_chat_user_player",
            columnNames = {"telegram_chat_id", "telegram_user_id", "steam_id"}
        ),
        @UniqueConstraint(
            name = "uk_chat_steam",
            columnNames = {"telegram_chat_id", "steam_id"}
        )
    },
    indexes = {
        @Index(name = "idx_csb_chat_user", columnList = "telegram_chat_id, telegram_user_id"),
        @Index(name = "idx_csb_steam", columnList = "steam_id")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatPlayerBindingEntity {
    
    @Id
    private UUID id;
    
    @Column(name = "telegram_chat_id", nullable = false)
    private Long telegramChatId;
    
    @Column(name = "telegram_user_id", nullable = false)
    private Long telegramUserId;
    
    @Column(name = "steam_id", nullable = false)
    private Long steamId;
    
    @Column(nullable = false)
    private boolean isPrimary = false;
    
    @Column(columnDefinition = "TEXT")
    private String notificationSettings;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    
    @Column(nullable = false)
    private OffsetDateTime updatedAt;
}

