package com.batrobot.user.application.mapper;

import com.batrobot.user.application.dto.request.UserRequest;
import com.batrobot.user.application.dto.response.UserResponse;
import com.batrobot.user.domain.model.User;
import com.batrobot.shared.domain.model.valueobject.TelegramUserId;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * MapStruct mapper for converting between Domain TelegramUser and DTOs.
 * 
 * This mapper handles Domain ↔ DTO conversions for application layer.
 * Follows DDD principle: Domain entities stay in domain, DTOs in application layer.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    
    /**
     * Creates a new TelegramUser domain entity from request.
     * 
     * @param request User request with Telegram data
     * @return New TelegramUser domain entity
     */
    default User createFromRequest(UserRequest request) {
        return User.create(
            TelegramUserId.of(request.getTelegramUserId()),
            request.getUsername(),
            request.getFirstName(),
            request.getLastName()
        );
    }

    /**
     * Converts Domain TelegramUser entity to TelegramUserResponse.
     * 
     * @param user Domain entity
     * @return TelegramUserResponse
     */
    @Mapping(target = "telegramUserId", source = "telegramUserId.value")
    UserResponse toResponse(User user);
}

