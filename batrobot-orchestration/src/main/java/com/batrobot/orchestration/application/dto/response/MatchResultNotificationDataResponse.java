package com.batrobot.orchestration.application.dto.response;

import java.util.List;

/**
 * Response containing notification target data for match result notifications.
 */
public record MatchResultNotificationDataResponse(
        Long matchId,
        Long startDateTime,
        Long endDateTime,
        List<MatchNotificationTarget> targets) {

    /**
     * A single notification target: a chat, the user who has the Steam account
     * bound,
     * and the Steam username of the player.
     */
    public record MatchNotificationTarget(
            Long telegramChatId,
            Long telegramUserId,
            String telegramUsername,
            String firstName,
            String lastName,
            String steamUsername,
            String lobbyType,
            String gameMode) {
    }
}
