package com.batrobot.chat.application.mapper;

import com.batrobot.chat.application.dto.request.ChatRequest;
import com.batrobot.chat.application.dto.response.ChatResponse;
import com.batrobot.chat.domain.model.Chat;
import com.batrobot.chat.domain.model.ChatType;
import com.batrobot.shared.domain.model.valueobject.TelegramChatId;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * MapStruct mapper for converting between Domain TelegramChat and DTOs.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatMapper {

    /**
     * Creates a new TelegramChat domain entity from request.
     * 
     * @param request Chat request with Telegram data
     * @return New TelegramChat domain entity
     */
    default Chat createFromRequest(ChatRequest request) {
        return Chat.create(
                TelegramChatId.of(request.getTelegramChatId()),
                ChatType.of(request.getType()),
                request.getTitle());
    }

    /**
     * Converts Domain TelegramChat entity to TelegramChatResponse.
     * 
     * @param chat Domain entity
     * @return Application Response DTO
     */
    @Mapping(target = "chatId", source = "telegramChatId.value")
    ChatResponse toResponse(Chat chat);
}
