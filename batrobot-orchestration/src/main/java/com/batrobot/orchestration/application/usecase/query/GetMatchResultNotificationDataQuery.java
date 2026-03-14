package com.batrobot.orchestration.application.usecase.query;

import com.batrobot.binding.application.dto.response.ChatPlayerBindingResponse;
import com.batrobot.binding.application.usecase.query.GetBindingsForSteamAccountQuery;
import com.batrobot.match.application.dto.response.MatchResponse;
import com.batrobot.match.application.usecase.query.GetMatchesByMatchIdsQuery;
import com.batrobot.orchestration.application.dto.response.MatchResultNotificationDataResponse;
import com.batrobot.orchestration.application.dto.response.MatchResultNotificationDataResponse.MatchNotificationTarget;
import com.batrobot.orchestration.application.mapper.OrchestrationResponseMapper;
import com.batrobot.player.application.dto.response.PlayerResponse;
import com.batrobot.player.application.usecase.query.GetPlayerBySteamIdQuery;
import com.batrobot.user.application.dto.response.UserResponse;
import com.batrobot.user.application.usecase.query.GetUsersByTelegramUserIdsQuery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Orchestration: resolves notification targets for a match result notification.
 * Given a Steam ID, finds all chats where the account is bound,
 * the Telegram users who own those bindings, and the Steam username.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class GetMatchResultNotificationDataQuery {

    private final GetBindingsForSteamAccountQuery getBindingsForSteamAccountQuery;
    private final GetUsersByTelegramUserIdsQuery getUsersByTelegramUserIdsQuery;
    private final GetPlayerBySteamIdQuery getPlayerBySteamIdQuery;
    private final GetMatchesByMatchIdsQuery getMatchesByMatchIdsQuery;
    private final OrchestrationResponseMapper responseMapper;

    /**
     * Resolves all notification targets for a match result event.
     *
     * @param steamId64 64-bit Steam ID of the player
     * @param matchId   external Dota 2 match ID
     * @return notification data with all targets (chat + user + steam username)
     */
    public MatchResultNotificationDataResponse execute(@NotNull Long steamId64, @NotNull Long matchId) {
        log.debug("Resolving match result notification targets for Steam account {}", steamId64);

        // Step 1: Get all bindings for this Steam account
        List<ChatPlayerBindingResponse> bindings = getBindingsForSteamAccountQuery.execute(steamId64);
        if (bindings.isEmpty()) {
            log.debug("No bindings found for Steam account {}, no notifications to send", steamId64);
            return new MatchResultNotificationDataResponse(matchId, null, null, List.of());
        }

        // Step 2: Resolve Steam username
        String steamUsername = getPlayerBySteamIdQuery.execute(steamId64)
                .map(PlayerResponse::steamUsername)
                .orElse(null);

        // Step 3: Resolve match start time
        Map<Long, MatchResponse> matches = getMatchesByMatchIdsQuery.execute(List.of(matchId));
        Long startDateTime = matches.containsKey(matchId) ? matches.get(matchId).startDateTime() : null;
        Long endDateTime = matches.containsKey(matchId) ? matches.get(matchId).endDateTime() : null;
        String lobbyType = matches.containsKey(matchId) ? matches.get(matchId).lobbyType() : null;
        String gameMode = matches.containsKey(matchId) ? matches.get(matchId).gameMode() : null;

        // Step 4: Collect unique Telegram user IDs and fetch user info
        List<Long> uniqueUserIds = bindings.stream()
                .map(ChatPlayerBindingResponse::telegramUserId)
                .distinct()
                .toList();

        Map<Long, UserResponse> usersById = getUsersByTelegramUserIdsQuery.execute(uniqueUserIds)
                .stream()
                .collect(Collectors.toMap(UserResponse::telegramUserId, Function.identity()));

        // Step 5: Build notification targets
        List<MatchNotificationTarget> targets = bindings.stream()
                .map(binding -> {
                    UserResponse user = usersById.get(binding.telegramUserId());
                    return responseMapper.toMatchNotificationTarget(
                            binding,
                            user,
                            steamUsername,
                            lobbyType,
                            gameMode);
                })
                .toList();

        log.debug("Resolved {} match result notification targets for Steam account {}",
                targets.size(), steamId64);
        return new MatchResultNotificationDataResponse(matchId, startDateTime, endDateTime, targets);
    }
}
