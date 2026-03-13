package com.batrobot.orchestration.application.dto.response;

import java.util.List;

/**
 * Response containing notification target data for player-related notifications.
 */
public record PlayerNotificationDataResponse(
        List<NotificationTarget> targets) {

    /**
     * A single notification target: a chat and the user who has the Steam account bound.
     */
    public record NotificationTarget(
            Long telegramChatId,
            Long telegramUserId,
            String telegramUsername) {
    }
}
