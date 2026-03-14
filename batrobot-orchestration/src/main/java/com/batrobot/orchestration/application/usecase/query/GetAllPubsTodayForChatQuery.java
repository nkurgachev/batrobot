package com.batrobot.orchestration.application.usecase.query;

import com.batrobot.binding.application.dto.response.ChatPlayerBindingResponse;
import com.batrobot.binding.application.usecase.query.GetBindingsByChatQuery;
import com.batrobot.chat.application.dto.response.ChatResponse;
import com.batrobot.chat.application.usecase.query.GetChatByTelegramChatIdQuery;
import com.batrobot.match.application.dto.response.MatchResponse;
import com.batrobot.match.application.usecase.query.GetRecentMatchesQuery;

import com.batrobot.player.application.dto.response.PlayerResponse;
import com.batrobot.player.application.usecase.query.GetPlayersBySteamIdsQuery;

import com.batrobot.playerstats.application.dto.response.PlayerMatchStatsResponse;
import com.batrobot.playerstats.application.usecase.query.GetStatsForPlayersInMatchesQuery;
import com.batrobot.user.application.dto.response.UserResponse;
import com.batrobot.user.application.usecase.query.GetUserByTelegramUserIdQuery;
import com.batrobot.orchestration.application.dto.response.AllPubsTodayCommandResponse;
import com.batrobot.orchestration.application.dto.response.AllPubsTodayCommandResponse.UserMatchHistory;
import com.batrobot.orchestration.application.dto.response.AllPubsTodayCommandResponse.UserMatchHistory.PlayerMatchHistory;
import com.batrobot.orchestration.application.dto.response.AllPubsTodayCommandResponse.UserMatchHistory.PlayerMatchHistory.MatchStats;
import com.batrobot.orchestration.application.exception.OrchestrationNoAccountsException;
import com.batrobot.orchestration.application.exception.OrchestrationAllPubsTodayNoMatchesException;
import com.batrobot.orchestration.application.exception.OrchestrationUserNotFoundException;
import com.batrobot.orchestration.application.mapper.OrchestrationResponseMapper;
import com.batrobot.shared.application.port.config.AppDayTimeConfig;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Orchestration: fetches today's matches for given players.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class GetAllPubsTodayForChatQuery {

    private final GetBindingsByChatQuery getBindingsByChatQuery;
    private final GetRecentMatchesQuery getRecentMatchesQuery;
    private final GetStatsForPlayersInMatchesQuery getStatsForPlayersInMatchesQuery;
    private final GetPlayersBySteamIdsQuery getPlayersBySteamIds;
    private final GetChatByTelegramChatIdQuery getChatByTelegramChatIdQuery;
    private final GetUserByTelegramUserIdQuery getUserByTelegramUserIdQuery;

    private final AppDayTimeConfig dayTimeConfigPort;
    private final OrchestrationResponseMapper responseMapper;

    /**
     * Fetches today's matches for specified players, grouped by Telegram user.
     *
     * @param chatId Telegram chat ID to fetch matches for
     * @return list of user-grouped match responses, each user with their bound
     *         accounts' matches
     * @throws OrchestrationNoAccountsException            if no players are bound
     *                                                     to the chat
     * @throws OrchestrationUserNotFoundException          if a Telegram user is not
     *                                                     found
     * @throws OrchestrationAllPubsTodayNoMatchesException if no matches are found
     *                                                     for any bound players
     *                                                     today
     */
    public AllPubsTodayCommandResponse execute(@NotNull Long chatId)
            throws OrchestrationNoAccountsException,
            OrchestrationUserNotFoundException,
            OrchestrationAllPubsTodayNoMatchesException {
        log.debug("Executing GetAllPubsTodayForChatQuery for chat {}", chatId);

        String chatTitle;

        // Step 1: Get all bindings for this chat
        List<ChatPlayerBindingResponse> bindings = getBindingsByChatQuery.execute(chatId);
        if (bindings.isEmpty()) {
            chatTitle = fetchChatTitle(chatId);
            throw new OrchestrationNoAccountsException(chatId, chatTitle);
        }

        List<Long> steamIds = bindings.stream()
                .map(binding -> binding.steamId64())
                .toList();

        long dayStartTimestamp = calculateDayStartTimestamp();

        // Step 2: Get matches that started after dayStart
        List<MatchResponse> recentMatches = getRecentMatchesQuery.execute(dayStartTimestamp);

        if (recentMatches.isEmpty()) {
            chatTitle = fetchChatTitle(chatId);
            throw new OrchestrationAllPubsTodayNoMatchesException(chatId, chatTitle);
        }

        Map<Long, MatchResponse> matchesMap = recentMatches.stream()
                .collect(Collectors.toMap(MatchResponse::matchId, m -> m));

        List<Long> matchIds = recentMatches.stream()
                .map(MatchResponse::matchId)
                .toList();

        // Step 3: Get player stats only for recent matches and our players
        List<PlayerMatchStatsResponse> statsList = getStatsForPlayersInMatchesQuery
                .execute(matchIds, steamIds);

        if (statsList.isEmpty()) {
            chatTitle = fetchChatTitle(chatId);
            throw new OrchestrationAllPubsTodayNoMatchesException(chatId, chatTitle);
        }

        // Step 4: Fetch player info
        List<Long> uniqueSteamIds = statsList.stream()
                .map(PlayerMatchStatsResponse::steamId64)
                .distinct()
                .toList();

        Map<Long, PlayerResponse> playersMap = getPlayersBySteamIds.execute(uniqueSteamIds)
                .stream()
                .collect(Collectors.toMap(PlayerResponse::steamId64, p -> p));

        // Step 5: Group stats by player and build responses
        Map<Long, List<PlayerMatchStatsResponse>> groupedByPlayer = statsList.stream()
                .collect(Collectors.groupingBy(PlayerMatchStatsResponse::steamId64));

        List<PlayerMatchHistory> playerResponses = groupedByPlayer.entrySet().stream()
                .map(entry -> buildPlayerResponse(entry.getKey(), entry.getValue(), matchesMap,
                        playersMap))
                .sorted(Comparator.comparing(
                        r -> r.steamUsername() != null ? r.steamUsername() : "",
                        String.CASE_INSENSITIVE_ORDER))
                .toList();

        // Step 6: Group by Telegram user
        List<UserMatchHistory> userGroups = groupMatchesByTelegramUser(bindings, playerResponses);

        log.debug("Found {} users with matches", userGroups.size());
        return responseMapper.toAllPubsTodayCommandResponse(userGroups);
    }

    private long calculateDayStartTimestamp() {
        ZoneId zoneId = ZoneId.of(dayTimeConfigPort.getTimezone());
        LocalDateTime now = LocalDateTime.now(zoneId);
        LocalDateTime dayStart = now.toLocalDate()
                .atTime(LocalTime.of(dayTimeConfigPort.getStartHour(), 0));

        if (now.isBefore(dayStart)) {
            dayStart = dayStart.minusDays(1);
        }

        long timestamp = dayStart.atZone(zoneId).toEpochSecond();

        log.debug("Day start: {} in {} timezone (timestamp: {})",
                dayStart, dayTimeConfigPort.getTimezone(), timestamp);

        return timestamp;
    }

    /**
     * Groups player matches by Telegram user.
     * <p>
     * Each Telegram user can have multiple Steam accounts.
     * This method creates a structure where matches are grouped by user,
     * with each user having a list of their Steam accounts' matches.
     *
     * @param bindings        list of Steam bindings with user and account info
     * @param playerResponses list of match data for each Steam account
     * @return list of matches grouped by Telegram user
     */
    private List<UserMatchHistory> groupMatchesByTelegramUser(
            List<ChatPlayerBindingResponse> bindings,
            List<PlayerMatchHistory> playerResponses) {
        // Create mapping: steamId -> PlayerTodayMatchesResponse
        Map<Long, PlayerMatchHistory> matchesBySteamId = new LinkedHashMap<>();
        for (PlayerMatchHistory playerMatches : playerResponses) {
            matchesBySteamId.put(playerMatches.steamId64(), playerMatches);
        }

        // Group by Telegram user (preserving user order from bindings)
        Map<Long, List<PlayerMatchHistory>> userAccountsMap = new LinkedHashMap<>();

        for (ChatPlayerBindingResponse binding : bindings) {
            Long steamId = binding.steamId64();
            PlayerMatchHistory playerMatches = matchesBySteamId.get(steamId);

            // Skip accounts with no matches
            if (playerMatches == null) {
                continue;
            }

            Long userId = binding.telegramUserId();
            userAccountsMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(playerMatches);
        }

        return userAccountsMap.entrySet().stream()
                .map(entry -> {
                    Long userId = entry.getKey();
                    UserResponse user = getUserByTelegramUserIdQuery.execute(userId)
                            .orElseThrow(() -> new OrchestrationUserNotFoundException(userId));
                    return responseMapper.toUserMatchHistory(user, entry.getValue());
                })
                .toList();
    }

    private PlayerMatchHistory buildPlayerResponse(
            Long steamId,
            List<PlayerMatchStatsResponse> playerStats,
            Map<Long, MatchResponse> matchesMap,
            Map<Long, PlayerResponse> playersMap) {

        PlayerResponse playerInfo = playersMap.get(steamId);

        List<MatchStats> matches = playerStats.stream()
                .sorted(Comparator.comparing(
                        stats -> {
                            MatchResponse match = matchesMap.get(stats.matchId());
                            return match != null && match.startDateTime() != null
                                    ? match.startDateTime()
                                    : 0L;
                        }))
                .map(stats -> {
                    MatchResponse match = matchesMap.get(stats.matchId());
                    return responseMapper.toMatchWithPlayerStats(match, stats);
                })
                .toList();

        return responseMapper.toPlayerTodayMatchesResponse(playerInfo, matches);
    }

    private String fetchChatTitle(Long chatId) {
        return getChatByTelegramChatIdQuery.execute(chatId)
                .map(ChatResponse::title)
                .orElse("Unknown");
    }
}
