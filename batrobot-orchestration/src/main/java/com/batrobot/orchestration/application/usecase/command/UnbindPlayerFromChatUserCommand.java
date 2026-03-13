package com.batrobot.orchestration.application.usecase.command;

import com.batrobot.binding.application.dto.request.DeleteBindingRequest;
import com.batrobot.binding.application.dto.response.ChatPlayerBindingResponse;
import com.batrobot.binding.application.exception.PlayerBindingNotFoundException;
import com.batrobot.binding.application.usecase.command.DeleteBinding;

import com.batrobot.player.application.dto.response.PlayerResponse;
import com.batrobot.player.application.usecase.query.GetPlayerBySteamIdQuery;
import com.batrobot.user.application.dto.response.UserResponse;
import com.batrobot.user.application.usecase.query.GetUserByTelegramUserIdQuery;
import com.batrobot.orchestration.application.dto.request.UnbindCommandRequest;
import com.batrobot.orchestration.application.dto.response.UnbindCommandResponse;
import com.batrobot.orchestration.application.exception.OrchestrationPlayerBindingNotFoundException;
import com.batrobot.orchestration.application.mapper.OrchestrationRequestMapper;
import com.batrobot.orchestration.application.mapper.OrchestrationResponseMapper;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

/**
 * Orchestration Use Case for unbinding Player from Telegram chat/user.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class UnbindPlayerFromChatUserCommand {

    private final GetPlayerBySteamIdQuery getPlayerBySteamId;
    private final GetUserByTelegramUserIdQuery getUserByTelegramUserIdQuery;

    private final DeleteBinding deleteBinding;

    private final OrchestrationRequestMapper orchestrationRequestMapper;
    private final OrchestrationResponseMapper orchestrationResponseMapper;

    /**
     * Deletes Player binding for Telegram user in chat.
     * 
     * @param request UnbindCommandRequest containing chat, user, and steamId64
     * @return UnbindCommandResponse with essential data for Telegram bot message
     * @throws OrchestrationPlayerBindingNotFoundException if binding not found
     */
    public UnbindCommandResponse execute(@Valid UnbindCommandRequest request)
            throws OrchestrationPlayerBindingNotFoundException {

        Long telegramChatId = request.getChat().getTelegramChatId();
        Long telegramUserId = request.getUser().getTelegramUserId();
        Long steamId64 = request.getSteamId64();

        Optional<PlayerResponse> player = getPlayerBySteamId.execute(steamId64);

        log.debug("Executing unbind: steamId={}, userId={}, chatId={}",
                steamId64, telegramUserId, telegramChatId);

        DeleteBindingRequest deleteRequest = orchestrationRequestMapper.toDeleteBindingRequest(request);
        ChatPlayerBindingResponse removedBinding;
        try {
            removedBinding = deleteBinding.execute(deleteRequest);
        } catch (PlayerBindingNotFoundException e) {
            String telegramUsername = getUserByTelegramUserIdQuery.execute(e.getTelegramUserId().value())
                    .map(UserResponse::username)
                    .orElse("unknown");
            String steamUsername = player
                    .map(PlayerResponse::steamUsername)
                    .orElse("unknown");
            throw new OrchestrationPlayerBindingNotFoundException(steamUsername, e.getSteamId(), telegramUsername);
        }

        log.info("Successfully deleted binding: id={}, steamId={}, userId={}, chatId={}",
                removedBinding.id(), steamId64, removedBinding.telegramUserId(), removedBinding.chatId());

        return orchestrationResponseMapper.toUnbindResponse(
                removedBinding,
                request.getUser(),
                player.orElse(null));
    }
}
