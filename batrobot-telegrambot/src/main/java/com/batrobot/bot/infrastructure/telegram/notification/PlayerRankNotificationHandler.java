package com.batrobot.bot.infrastructure.telegram.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.batrobot.bot.infrastructure.telegram.event.outbound.OutboundMessageRequestedEvent;
import com.batrobot.bot.infrastructure.telegram.formatter.notification.RankChangeNotificationFormatter;
import com.batrobot.orchestration.application.dto.response.PlayerNotificationDataResponse;
import com.batrobot.orchestration.application.dto.response.PlayerNotificationDataResponse.NotificationTarget;
import com.batrobot.orchestration.application.usecase.query.GetPlayerNotificationDataQuery;
import com.batrobot.player.domain.event.PlayerRankUpdatedEvent;

/**
 * Event handler for player rank changes.
 * Sends notification messages to all chats where the player is bound.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PlayerRankNotificationHandler {

    private final GetPlayerNotificationDataQuery getPlayerNotificationDataQuery;
    private final RankChangeNotificationFormatter presenter;
    private final ApplicationEventPublisher eventPublisher;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void notifyOnSeasonRankUpdated(PlayerRankUpdatedEvent event) {
        log.info("Player rank changed: steamId={}, oldRank={}, newRank={}, occurredAt={}",
                event.steamId(),
                event.oldSeasonRank(),
                event.newSeasonRank(),
                event.occurredAt());

        PlayerNotificationDataResponse notificationData =
                getPlayerNotificationDataQuery.execute(event.steamId().value());

        if (notificationData.targets().isEmpty()) {
            log.debug("No notification targets for steamId={}", event.steamId());
            return;
        }

        for (NotificationTarget target : notificationData.targets()) {
            String message = presenter.formatResult(target, event.oldSeasonRank(), event.newSeasonRank());
            eventPublisher.publishEvent(new OutboundMessageRequestedEvent(
                    target.telegramChatId(), null, message));
            log.debug("Sent rank change notification to chat={}", target.telegramChatId());
        }
    }
}