package com.batrobot.user.infrastructure.persistence.mapper;

import com.batrobot.user.domain.model.User;
import com.batrobot.user.infrastructure.persistence.entity.UserEntity;
import com.batrobot.shared.domain.model.valueobject.TelegramUserId;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * Mapper for converting between JPA TelegramUser entity and Domain TelegramUser.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserEntityMapper {
    
    /**
     * Converts JPA entity to Domain TelegramUser.
     * Reconstitutes domain entity with all state from persistence.
     * 
     * @param entity JPA entity
     * @return Domain entity
     */
    default User toDomain(UserEntity entity) {
        TelegramUserId telegramUserId = TelegramUserId.of(entity.getTelegramUserId());
        
        return User.reconstitute(
            entity.getId(),
            telegramUserId,
            entity.getUsername(),
            entity.getFirstName(),
            entity.getLastName(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    /**
     * Converts Domain TelegramUser to JPA entity.
     * 
     * @param domain Domain entity
     * @return JPA entity
     */
    @Mapping(target = "telegramUserId", source = "telegramUserId.value")
    UserEntity toEntity(User domain);
}

