package com.batrobot.orchestration.application.usecase.query;

import com.batrobot.binding.application.dto.response.ChatPlayerBindingResponse;
import com.batrobot.binding.application.usecase.query.GetBindingsByChatQuery;
import com.batrobot.chat.application.dto.response.ChatResponse;
import com.batrobot.chat.application.usecase.query.GetChatByTelegramChatIdQuery;
import com.batrobot.player.application.dto.response.PlayerResponse;
import com.batrobot.player.application.usecase.query.GetPlayersBySteamIdsQuery;

import com.batrobot.user.application.dto.response.UserResponse;
import com.batrobot.user.application.usecase.query.GetUsersByTelegramUserIdsQuery;

import com.batrobot.orchestration.application.dto.response.TopPubersResponse;
import com.batrobot.orchestration.application.dto.response.TopPubersResponse.PuberInfo;
import com.batrobot.orchestration.application.dto.response.TopPubersResponse.RankGroup;
import com.batrobot.orchestration.application.exception.OrchestrationNoAccountsException;
import com.batrobot.orchestration.application.mapper.OrchestrationResponseMapper;

import jakarta.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Query Use Case for fetching top pubers in a chat.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class GetTopPubersForChatQuery {

    private final GetBindingsByChatQuery getBindingsByChatQuery;
    private final GetUsersByTelegramUserIdsQuery getUsersByTelegramUserIdsQuery;
    private final GetPlayersBySteamIdsQuery getPlayersBySteamIdsQuery;
    private final GetChatByTelegramChatIdQuery getChatByTelegramChatIdQuery;
    private final OrchestrationResponseMapper responseMapper;

    /**
     * Fetches all players in the chat with their user and rank info.
     *
     * @param chatId Telegram chat ID
     * @return TopPubersResponse with all bound players
     */
    public TopPubersResponse execute(@NotNull Long chatId) {
        log.debug("Fetching top pubers for chat {}", chatId);

        String chatTitle;

        List<ChatPlayerBindingResponse> bindings = getBindingsByChatQuery.execute(chatId);
        if (bindings.isEmpty()) {
            chatTitle = getChatByTelegramChatIdQuery.execute(chatId)
                .map(ChatResponse::title)
                .orElse("Unknown");
            throw new OrchestrationNoAccountsException(chatId, chatTitle);
        }

        List<Long> steamIds = bindings.stream()
                .map(ChatPlayerBindingResponse::steamId64)
                .distinct()
                .toList();
        List<Long> userIds = bindings.stream()
                .map(ChatPlayerBindingResponse::telegramUserId)
                .distinct()
                .toList();

        Map<Long, PlayerResponse> playersBySteamId = getPlayersBySteamIdsQuery.execute(steamIds)
                .stream()
                .collect(Collectors.toMap(PlayerResponse::steamId64, Function.identity()));

        Map<Long, UserResponse> usersById = getUsersByTelegramUserIdsQuery.execute(userIds)
                .stream()
                .collect(Collectors.toMap(UserResponse::telegramUserId, Function.identity()));

        List<PuberInfo> pubers = bindings.stream()
                .map(binding -> {
                    UserResponse user = usersById.get(binding.telegramUserId());
                    PlayerResponse player = playersBySteamId.get(binding.steamId64());

                    return responseMapper.toPuberInfo(binding, user, player);
                })
                .toList();

        List<RankGroup> rankGroups = groupBySeasonRank(pubers);
        return new TopPubersResponse(rankGroups);
    }

    /**
     * Groups pubers by seasonal rank.
     * Ranks sorted descending (highest first), null ranks last.
     * Players within each group sorted alphabetically by Telegram username.
     */
    private List<RankGroup> groupBySeasonRank(List<PuberInfo> pubers) {
        Map<Integer, List<PuberInfo>> grouped = new LinkedHashMap<>();
        for (PuberInfo puber : pubers) {
            grouped.computeIfAbsent(puber.seasonRank(), k -> new ArrayList<>()).add(puber);
        }

        List<Integer> sortedRanks = grouped.keySet().stream()
                .filter(Objects::nonNull)
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());
        if (grouped.containsKey(null)) {
            sortedRanks.add(null);
        }

        return sortedRanks.stream()
                .map(rank -> {
                    List<PuberInfo> players = grouped.get(rank).stream()
                            .sorted(Comparator.comparing(
                                    p -> p.telegramUsername() != null ? p.telegramUsername() : ""))
                            .toList();
                    return new RankGroup(rank, players);
                })
                .toList();
    }
}
