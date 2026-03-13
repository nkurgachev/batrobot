package com.batrobot.chat.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "chats", indexes = {
    @Index(name = "idx_chats_updated", columnList = "updated_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatEntity {
    
    @Id
    private UUID id;
    
    @Column(name = "telegram_chat_id", unique = true, nullable = false)
    private Long telegramChatId;
    
    @Enumerated(EnumType.STRING)
    private ChatType type;
    
    private String title;
    
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    
    @Column(nullable = false)
    private OffsetDateTime updatedAt;
    
    public enum ChatType {
        PRIVATE,
        GROUP,
        SUPERGROUP
    }
}



