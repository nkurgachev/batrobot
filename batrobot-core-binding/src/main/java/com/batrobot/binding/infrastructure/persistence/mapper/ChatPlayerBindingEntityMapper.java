package com.batrobot.binding.infrastructure.persistence.mapper;

import com.batrobot.binding.domain.model.ChatPlayerBinding;
import com.batrobot.binding.infrastructure.persistence.entity.ChatPlayerBindingEntity;
import com.batrobot.shared.domain.model.valueobject.SteamId;
import com.batrobot.shared.domain.model.valueobject.TelegramChatId;
import com.batrobot.shared.domain.model.valueobject.TelegramUserId;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

/**
 * Mapper between JPA entity (ChatSteamBinding) and Domain entity (ChatPlayerBinding).
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatPlayerBindingEntityMapper {
    
    /**
     * Maps from JPA entity to domain entity.
     * 
     * @param entity JPA entity
     * @return Domain entity
     */
    default ChatPlayerBinding toDomain(ChatPlayerBindingEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return ChatPlayerBinding.reconstitute(
                entity.getId(),
                TelegramChatId.of(entity.getTelegramChatId()),
                TelegramUserId.of(entity.getTelegramUserId()),
                SteamId.fromSteamId64(entity.getSteamId()),
                entity.isPrimary(),
                entity.getNotificationSettings(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
    
    /**
     * Maps from domain entity to JPA entity.
     * 
     * @param domain Domain entity
     * @return JPA entity
     */
    @Mapping(target = "telegramChatId", source = "chatId.value")
    @Mapping(target = "telegramUserId", source = "userId.value")
    @Mapping(target = "steamId", source = "steamId.value")
    @Mapping(target = "primary", source = "isPrimary", qualifiedByName = "booleanOrFalse")
    ChatPlayerBindingEntity toEntity(ChatPlayerBinding domain);

    @Named("booleanOrFalse")
    default boolean booleanOrFalse(Boolean value) {
        return value != null && value;
    }
}

