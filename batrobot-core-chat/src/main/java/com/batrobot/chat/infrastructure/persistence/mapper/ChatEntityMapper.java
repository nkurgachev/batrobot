package com.batrobot.chat.infrastructure.persistence.mapper;

import com.batrobot.chat.domain.model.Chat;
import com.batrobot.chat.domain.model.ChatType;
import com.batrobot.chat.infrastructure.persistence.entity.ChatEntity;
import com.batrobot.shared.domain.model.valueobject.TelegramChatId;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

/**
 * Mapper for converting between JPA Chat entity and Domain Chat.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatEntityMapper {

     /**
     * Converts JPA entity to Domain Chat.
     * 
     * @param entity JPA entity
     * @return Domain entity
     */
    default Chat toDomain(ChatEntity entity) {
        TelegramChatId telegramChatId = TelegramChatId.of(entity.getTelegramChatId());
        ChatType type = ChatType.valueOf(entity.getType().name());

        return Chat.reconstitute(
            entity.getId(),
            telegramChatId,
            type,
            entity.getTitle(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
    
    /**
     * Converts Domain Chat to JPA entity.
     * 
     * @param domain Domain entity
     * @return JPA entity
     */
    @Mapping(target = "telegramChatId", source = "telegramChatId.value")
    @Mapping(target = "type", source = "type", qualifiedByName = "chatTypeToEntityType")
    ChatEntity toEntity(Chat domain);

    @Named("chatTypeToEntityType")
    default ChatEntity.ChatType chatTypeToEntityType(ChatType type) {
        return type == null ? null : ChatEntity.ChatType.valueOf(type.name());
    }
}

