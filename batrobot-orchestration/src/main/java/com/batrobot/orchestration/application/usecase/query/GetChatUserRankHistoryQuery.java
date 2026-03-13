package com.batrobot.orchestration.application.usecase.query;

import com.batrobot.binding.application.dto.response.ChatPlayerBindingResponse;
import com.batrobot.binding.application.usecase.query.GetBindingsForUserInChatQuery;

import com.batrobot.player.application.dto.response.PlayerResponse;
import com.batrobot.player.application.usecase.query.GetPlayersBySteamIdsQuery;

import com.batrobot.rankhistory.application.dto.response.PlayerRankHistoryResponse;
import com.batrobot.rankhistory.application.usecase.query.GetRankHistoryForPlayersQuery;

import com.batrobot.orchestration.application.dto.request.CommonRequest;
import com.batrobot.orchestration.application.dto.response.MeCommandResponse;
import com.batrobot.orchestration.application.dto.response.MeCommandResponse.PlayerRankHistory;
import com.batrobot.orchestration.application.dto.response.MeCommandResponse.PlayerRankHistory.RankInfo;
import com.batrobot.orchestration.application.exception.OrchestrationUserNoAccountsException;
import com.batrobot.orchestration.application.mapper.OrchestrationRequestMapper;
import com.batrobot.orchestration.application.mapper.OrchestrationResponseMapper;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Query Use Case for fetching bindings with rank history.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class GetChatUserRankHistoryQuery {

    // Domain use cases
    private final GetBindingsForUserInChatQuery getBindingsForUserInChat;
    private final GetPlayersBySteamIdsQuery getPlayersBySteamIds;
    private final GetRankHistoryForPlayersQuery getRankHistoryForPlayers;

    // Mappers for DTO transformations
    private final OrchestrationRequestMapper orchestrationRequestMapper;
    private final OrchestrationResponseMapper orchestrationResponseMapper;

    /**
     * Fetches bindings for user in chat along with their rank history.
     * 
     * @param query CommonRequest containing chat and user information
     * @return MeCommandResponse with historical data
     * @throws OrchestrationUserNoAccountsException if no bindings are found for the user in the chat
     */
    public MeCommandResponse execute(@Valid CommonRequest query)
            throws OrchestrationUserNoAccountsException {
        Long chatId = orchestrationRequestMapper.toTelegramChatId(query);
        Long userId = orchestrationRequestMapper.toTelegramUserId(query);
        log.debug("Fetching players with rank history for user {} in chat {}", userId, chatId);

        List<ChatPlayerBindingResponse> bindings = getBindingsForUserInChat.execute(chatId, userId);
        if (bindings.isEmpty()) {
            log.debug("No bindings found for user {} in chat {}", userId, chatId);
            throw new OrchestrationUserNoAccountsException(chatId, userId);
        }

        List<Long> steamIds = bindings.stream()
                .map(ChatPlayerBindingResponse::steamId64)
                .distinct()
                .toList();

        List<PlayerResponse> players = getPlayersBySteamIds.execute(steamIds);
        Map<Long, PlayerResponse> playersBySteamId = players.stream()
                .collect(Collectors.toMap(PlayerResponse::steamId64, Function.identity()));

        List<PlayerRankHistoryResponse> historyRows = getRankHistoryForPlayers.execute(steamIds);
        Map<Long, List<RankInfo>> historyBySteamId = new LinkedHashMap<>();
        for (PlayerRankHistoryResponse historyRow : historyRows) {
            Long steamId = historyRow.steamId64();
            List<RankInfo> rankItems = historyRow.rankHistory().stream()
                    .map(orchestrationResponseMapper::toRankInfo)
                    .sorted(Comparator.comparing(RankInfo::assignedAt).reversed())
                    .toList();
            historyBySteamId.put(steamId, new ArrayList<>(rankItems));
        }

        List<PlayerRankHistory> result = bindings.stream()
                .map(binding -> {
                    Long steamId = binding.steamId64();
                    List<RankInfo> history = historyBySteamId.getOrDefault(steamId, Collections.emptyList());
                    PlayerResponse player = playersBySteamId.get(steamId);

                    if (player == null) {
                        return orchestrationResponseMapper.toPlayerRankHistoryFallback(steamId, "Unknown", history);
                    }

                    return orchestrationResponseMapper.toPlayerRankHistory(player, history);
                })
                .toList();

        log.debug("Found {} bindings with history for user {} in chat {}", result.size(), userId, chatId);
        return orchestrationResponseMapper.toMeCommandResponse(result);
    }
}
