package com.batrobot.orchestration.application.usecase.query;

import com.batrobot.steam.application.dto.response.SteamPlayerResponse;
import com.batrobot.steam.application.exception.SteamUnavailableException;
import com.batrobot.steam.application.usecase.query.GetSteamPlayersInGameQuery;

import com.batrobot.binding.application.dto.response.ChatPlayerBindingResponse;
import com.batrobot.binding.application.usecase.query.GetBindingsByChatQuery;
import com.batrobot.chat.application.dto.response.ChatResponse;
import com.batrobot.chat.application.usecase.query.GetChatByTelegramChatIdQuery;
import com.batrobot.user.application.dto.response.UserResponse;
import com.batrobot.user.application.usecase.query.GetUsersByTelegramUserIdsQuery;

import com.batrobot.orchestration.application.dto.response.InGameCommandResponse;
import com.batrobot.orchestration.application.dto.response.InGameCommandResponse.UserGameStatus;
import com.batrobot.orchestration.application.dto.response.InGameCommandResponse.UserGameStatus.GameInfo;
import com.batrobot.orchestration.application.exception.OrchestrationInGameNoGames;
import com.batrobot.orchestration.application.exception.OrchestrationNoAccountsException;
import com.batrobot.orchestration.application.exception.OrchestrationSteamUnavailableException;
import com.batrobot.orchestration.application.mapper.OrchestrationResponseMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;

/**
 * Orchestration Query Use Case for retrieving Steam players currently in-game.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class GetChatUsersInGameQuery {

    private final GetBindingsByChatQuery getBindingsByChatQuery;
    private final GetUsersByTelegramUserIdsQuery getUsersByTelegramUserIdsQuery;
    private final GetSteamPlayersInGameQuery getSteamPlayersInGameQuery;
    private final GetChatByTelegramChatIdQuery getChatByTelegramChatIdQuery;
    private final OrchestrationResponseMapper responseMapper;

    /**
     * Get players currently in-game for multiple Steam accounts.
     * Returns only players who are currently playing a game.
     * 
     * @param chatId Telegram chat ID to fetch players for
     * @return list of Steam player DTOs for players currently in-game with their game information
     * @throws OrchestrationNoAccountsException if no Steam accounts are linked to the chat
     */
    public InGameCommandResponse execute(@NotNull Long chatId)
            throws OrchestrationNoAccountsException,
            OrchestrationSteamUnavailableException,
            OrchestrationInGameNoGames {
        log.debug("Fetching game status for chat {}", chatId);

        String chatTitle;

        List<ChatPlayerBindingResponse> bindings = getBindingsByChatQuery.execute(chatId);
        if (bindings.isEmpty()) {
            chatTitle = fetchChatTitle(chatId);
            throw new OrchestrationNoAccountsException(chatId, chatTitle);
        }

        List<Long> steamIds = bindings.stream()
                .map(ChatPlayerBindingResponse::steamId64)
                .toList();

        List<UserResponse> users = getUsersByTelegramUserIdsQuery.execute(
                bindings.stream()
                        .map(binding -> binding.telegramUserId())
                        .distinct()
                        .toList());

        List<SteamPlayerResponse> playersInGame;
        try {
            playersInGame = getSteamPlayersInGameQuery.execute(steamIds);
        } catch (SteamUnavailableException e) {
            throw new OrchestrationSteamUnavailableException(e);
        }

        // Create map: steamId -> GameInfo for quick lookup
        Map<Long, GameInfo> gameInfoMap = new LinkedHashMap<>();
        for (SteamPlayerResponse player : playersInGame) {
            gameInfoMap.put(player.steamId64(), responseMapper.toGameInfo(player));
        }

        // Group game info by Telegram user based on bindings
        Map<Long, UserResponse> userMap = new LinkedHashMap<>();
        for (UserResponse user : users) {
            userMap.put(user.telegramUserId(), user);
        }

        Map<Long, List<GameInfo>> userGamesMap = new LinkedHashMap<>();

        for (ChatPlayerBindingResponse binding : bindings) {
            GameInfo gameInfo = gameInfoMap.get(binding.steamId64());

            if (gameInfo != null) {
                Long userId = binding.telegramUserId();
                userGamesMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(gameInfo);
            }
        }

        // Build response using mapper
        List<UserGameStatus> userStatuses = userGamesMap.entrySet().stream()
                .map(entry -> {
                    UserResponse user = userMap.get(entry.getKey());
                    return user != null
                            ? responseMapper.toUserGameStatus(user, entry.getValue())
                            : responseMapper.toUserGameStatusFallback(entry.getKey(), entry.getValue());
                })
                .toList();

        if (userStatuses.isEmpty()) {
            chatTitle = fetchChatTitle(chatId);
            throw new OrchestrationInGameNoGames(chatId, chatTitle);
        }

        return responseMapper.toInGameCommandResponse(userStatuses);
    }

    private String fetchChatTitle(Long chatId) {
        return getChatByTelegramChatIdQuery.execute(chatId)
                .map(ChatResponse::title)
                .orElse("Unknown");
    }
}
