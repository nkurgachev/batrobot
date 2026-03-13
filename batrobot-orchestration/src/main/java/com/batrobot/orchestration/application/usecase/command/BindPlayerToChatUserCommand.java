package com.batrobot.orchestration.application.usecase.command;

import com.batrobot.stratz.application.dto.response.StratzPlayerResponse;
import com.batrobot.stratz.application.exception.StratzUnavailableException;
import com.batrobot.stratz.application.usecase.query.GetPlayersFromStratzQuery;

import com.batrobot.binding.application.dto.request.ChatPlayerBindingRequest;
import com.batrobot.binding.application.dto.response.ChatPlayerBindingResponse;
import com.batrobot.binding.application.usecase.command.CreateBinding;
import com.batrobot.binding.domain.exception.PlayerAlreadyBoundException;

import com.batrobot.chat.application.dto.request.ChatRequest;
import com.batrobot.chat.application.dto.response.ChatResponse;
import com.batrobot.chat.application.usecase.command.UpsertChat;

import com.batrobot.player.application.dto.request.PlayerRequest;
import com.batrobot.player.application.dto.response.PlayerResponse;
import com.batrobot.player.application.usecase.command.UpsertPlayer;

import com.batrobot.user.application.dto.request.UserRequest;
import com.batrobot.user.application.dto.response.UserResponse;
import com.batrobot.user.application.usecase.command.UpsertUser;
import com.batrobot.user.application.usecase.query.GetUserByTelegramUserIdQuery;

import com.batrobot.orchestration.application.dto.request.BindCommandRequest;
import com.batrobot.orchestration.application.dto.response.BindCommandResponse;
import com.batrobot.orchestration.application.exception.OrchestrationPlayerAlreadyBoundException;
import com.batrobot.orchestration.application.exception.OrchestrationPlayerNotFoundException;
import com.batrobot.orchestration.application.exception.OrchestrationStratzUnavailableException;
import com.batrobot.orchestration.application.mapper.OrchestrationRequestMapper;
import com.batrobot.orchestration.application.mapper.OrchestrationResponseMapper;

import com.batrobot.shared.domain.model.valueobject.SteamId;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Orchestration Use Case for binding a Player to a Telegram user in a chat.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class BindPlayerToChatUserCommand {

    private final GetPlayersFromStratzQuery getPlayersFromStratzQuery;
    private final GetUserByTelegramUserIdQuery getUserByTelegramUserIdQuery;

    private final UpsertPlayer upsertPlayer;
    private final UpsertChat upsertChat;
    private final UpsertUser upsertUser;
    private final CreateBinding createBinding;

    private final OrchestrationRequestMapper orchestrationRequestMapper;
    private final OrchestrationResponseMapper orchestrationResponseMapper;

    /**
     * Binds a Player to a Telegram user in a chat.
     * 
     * @param request BindCommandRequest containing chat, user, and steamId64
     * @return BindCommandResponse with essential data for Telegram bot message
     * @throws OrchestrationPlayerNotFoundException if player not found
     * @throws OrchestrationPlayerAlreadyBoundException if player already bound to another user
     * @throws OrchestrationStratzUnavailableException if Stratz API is unavailable
     */
    public BindCommandResponse execute(@Valid BindCommandRequest request)
            throws
                OrchestrationPlayerNotFoundException,
                OrchestrationPlayerAlreadyBoundException,
                OrchestrationStratzUnavailableException {

        log.debug("Orchestrating binding: steamId={}, userId={}, chatId={}",
                request.getSteamId64(),
                request.getUser().getTelegramUserId(),
                request.getChat().getTelegramChatId());

        // Step 1: Fetch Player from Stratz API
        SteamId steamId = SteamId.fromSteamId64(request.getSteamId64());
        StratzPlayerResponse playerFromStratz;
        try {
            playerFromStratz = getPlayersFromStratzQuery.execute(List.of(steamId.value()))
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new OrchestrationPlayerNotFoundException(steamId));
        } catch (StratzUnavailableException e) {
            throw new OrchestrationStratzUnavailableException(e);
        }

        // Step 2: Сreate/update Player
        PlayerRequest playerRequest = orchestrationRequestMapper.toPlayerRequest(playerFromStratz);
        PlayerResponse player = upsertPlayer.execute(playerRequest);

        // Step 3: Create/update Chat
        ChatRequest chatRequest = orchestrationRequestMapper.toChatRequest(request.getChat());
        ChatResponse chat = upsertChat.execute(chatRequest);

        // Step 4: Create/update User
        UserRequest userRequest = orchestrationRequestMapper.toUserRequest(request.getUser());
        UserResponse user = upsertUser.execute(userRequest);

        // Step 5: Create binding between all three entities
        ChatPlayerBindingRequest bindingRequest = orchestrationRequestMapper.toChatPlayerBindingRequest(request);
        ChatPlayerBindingResponse binding;
        try {
            binding = createBinding.execute(bindingRequest);
        } catch (PlayerAlreadyBoundException e) {
            String telegramUsername = getUserByTelegramUserIdQuery.execute(e.getTelegramUserId().value())
                    .map(UserResponse::username)
                    .orElse("unknown");
            throw new OrchestrationPlayerAlreadyBoundException(player.steamUsername(), steamId, telegramUsername);
        }

        log.info("Successfully created binding: id={}, steamId={}, userId={}, chatId={}",
                binding.id(), steamId, userRequest.getTelegramUserId(),
                chatRequest.getTelegramChatId());

        // Step 6: Transform to BFF response with only essential data for client
        return orchestrationResponseMapper.toOrchestrationResponse(binding, chat, user, player);
    }
}


