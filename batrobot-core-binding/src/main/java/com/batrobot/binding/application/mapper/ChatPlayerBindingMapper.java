package com.batrobot.binding.application.mapper;

import com.batrobot.binding.application.dto.request.ChatPlayerBindingRequest;
import com.batrobot.binding.application.dto.response.ChatPlayerBindingResponse;
import com.batrobot.binding.domain.model.ChatPlayerBinding;
import com.batrobot.shared.domain.model.valueobject.SteamId;
import com.batrobot.shared.domain.model.valueobject.TelegramChatId;
import com.batrobot.shared.domain.model.valueobject.TelegramUserId;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * MapStruct mapper for converting between Domain ChatPlayerBinding and DTOs.
 * 
 * This mapper handles Domain → DTO conversions for application layer.
 * Follows DDD principle: Domain entities stay in domain, DTOs in application layer.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatPlayerBindingMapper {

    /**
     * Creates a new TelegramChat domain entity from request.
     * 
     * @param request Chat request with Telegram data
     * @return New TelegramChat domain entity
     */
    default ChatPlayerBinding createFromRequest(ChatPlayerBindingRequest request) {
        return ChatPlayerBinding.create(
                TelegramChatId.of(request.getTelegramChatId()),
                TelegramUserId.of(request.getTelegramUserId()),
                SteamId.fromSteamId64(request.getSteamId64()),
                request.isPrimary()
        );
    }
    
    /**
     * Converts Domain ChatPlayerBinding entity to ChatPlayerBindingResponse DTO.
     * 
     * @param chatPlayerBinding Domain entity
     * @return Application DTO
     */
    @Mapping(target = "chatId", source = "chatPlayerBinding.chatId.value")
    @Mapping(target = "telegramUserId", source = "chatPlayerBinding.userId.value")
    @Mapping(target = "steamId64", source = "chatPlayerBinding.steamId.value")
    ChatPlayerBindingResponse toResponse(ChatPlayerBinding chatPlayerBinding);
}

