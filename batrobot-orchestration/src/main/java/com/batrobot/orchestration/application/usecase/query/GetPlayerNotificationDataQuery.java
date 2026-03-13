package com.batrobot.orchestration.application.usecase.query;

import com.batrobot.binding.application.dto.response.ChatPlayerBindingResponse;
import com.batrobot.binding.application.usecase.query.GetBindingsForSteamAccountQuery;
import com.batrobot.orchestration.application.dto.response.PlayerNotificationDataResponse;
import com.batrobot.orchestration.application.dto.response.PlayerNotificationDataResponse.NotificationTarget;
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
 * Orchestration: resolves notification targets for player-related events.
 * Given a Steam ID, finds all chats where the account is bound
 * and the Telegram users who own those bindings.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class GetPlayerNotificationDataQuery {

    private final GetBindingsForSteamAccountQuery getBindingsForSteamAccountQuery;
    private final GetUsersByTelegramUserIdsQuery getUsersByTelegramUserIdsQuery;

    /**
     * Resolves all notification targets for a player-related event.
     *
     * @param steamId64 64-bit Steam ID of the player
     * @return notification data with all targets (chat + user pairs)
     */
    public PlayerNotificationDataResponse execute(@NotNull Long steamId64) {
        log.debug("Resolving notification targets for Steam account {}", steamId64);

        // Step 1: Get all bindings for this Steam account
        List<ChatPlayerBindingResponse> bindings = getBindingsForSteamAccountQuery.execute(steamId64);
        if (bindings.isEmpty()) {
            log.debug("No bindings found for Steam account {}, no notifications to send", steamId64);
            return new PlayerNotificationDataResponse(List.of());
        }

        // Step 2: Collect unique Telegram user IDs and fetch user info
        List<Long> uniqueUserIds = bindings.stream()
                .map(ChatPlayerBindingResponse::telegramUserId)
                .distinct()
                .toList();

        Map<Long, UserResponse> usersById = getUsersByTelegramUserIdsQuery.execute(uniqueUserIds)
                .stream()
                .collect(Collectors.toMap(UserResponse::telegramUserId, Function.identity()));

        // Step 3: Build notification targets
        List<NotificationTarget> targets = bindings.stream()
                .map(binding -> {
                    UserResponse user = usersById.get(binding.telegramUserId());
                    return new NotificationTarget(
                            binding.chatId(),
                            binding.telegramUserId(),
                            user != null ? user.username() : null);
                })
                .toList();

        log.debug("Resolved {} notification targets for Steam account {}", targets.size(), steamId64);
        return new PlayerNotificationDataResponse(targets);
    }
}
