package com.batrobot.orchestration.application.mapper;

import com.batrobot.stratz.application.dto.response.StratzPlayerResponse;

import com.batrobot.binding.application.dto.request.ChatPlayerBindingRequest;
import com.batrobot.binding.application.dto.request.DeleteBindingRequest;
import com.batrobot.chat.application.dto.request.ChatRequest;
import com.batrobot.player.application.dto.request.PlayerRequest;
import com.batrobot.user.application.dto.request.UserRequest;

import com.batrobot.orchestration.application.dto.request.BindCommandRequest;
import com.batrobot.orchestration.application.dto.request.CommonRequest;
import com.batrobot.orchestration.application.dto.request.UnbindCommandRequest;
import com.batrobot.orchestration.application.dto.request.info.ChatInfo;
import com.batrobot.orchestration.application.dto.request.info.UserInfo;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * MapStruct mapper for converting between orchestration DTOs and domain request DTOs.
 * 
 * Converts compact orchestration layer DTOs (ChatInfo, UserInfo) 
 * to domain-specific request DTOs (ChatRequest, UserRequest) for use case execution.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrchestrationRequestMapper {

    /**
     * Converts orchestration ChatInfo to domain ChatRequest.
     * 
     * @param chatInfo Orchestration DTO with chat information
     * @return Domain request DTO for chat use cases
     */
    ChatRequest toChatRequest(ChatInfo chatInfo);

    /**
     * Converts orchestration UserInfo to domain UserRequest.
     * 
     * @param userInfo Orchestration DTO with user information
     * @return Domain request DTO for user use cases
     */
    UserRequest toUserRequest(UserInfo userInfo);

    @Mapping(target = "telegramChatId", source = "chat.telegramChatId")
    @Mapping(target = "telegramUserId", source = "user.telegramUserId")
    @Mapping(target = "steamId64", source = "steamId64")
    @Mapping(target = "primary", source = "primaryBinding")
    ChatPlayerBindingRequest toChatPlayerBindingRequest(BindCommandRequest request);

    @Mapping(target = "chatId", source = "chat.telegramChatId")
    @Mapping(target = "userId", source = "user.telegramUserId")
    @Mapping(target = "steamId64", source = "steamId64")
    DeleteBindingRequest toDeleteBindingRequest(UnbindCommandRequest request);

    /**
     * Maps StratzPlayerResponse (intermediate DTO from gateway) to PlayerRequest.
     * Used when creating/updating a Player based on Stratz data.
     *
     * @param dto The Stratz player response (intermediate DTO)
     * @return PlayerRequest with fields mapped from StratzPlayerResponse
     */
    @Mapping(target = "accountCreationDate", source = "timeCreated")
    PlayerRequest toPlayerRequest(StratzPlayerResponse dto);

    /**
     * Extracts Telegram chat ID from orchestration request.
     *
     * @param request User and chat request
     * @return Telegram chat ID
     */
    default Long toTelegramChatId(CommonRequest request) {
        return request.getChat().getTelegramChatId();
    }

    /**
     * Extracts Telegram user ID from orchestration request.
     *
     * @param request User and chat request
     * @return Telegram user ID
     */
    default Long toTelegramUserId(CommonRequest request) {
        return request.getUser().getTelegramUserId();
    }
}


