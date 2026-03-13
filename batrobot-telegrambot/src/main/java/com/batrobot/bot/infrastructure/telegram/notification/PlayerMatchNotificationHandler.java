package com.batrobot.bot.infrastructure.telegram.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.batrobot.bot.infrastructure.telegram.event.outbound.OutboundMessageRequestedEvent;
import com.batrobot.bot.infrastructure.telegram.formatter.notification.MatchResultNotificationFormatter;
import com.batrobot.orchestration.application.dto.response.MatchResultNotificationDataResponse;
import com.batrobot.orchestration.application.dto.response.MatchResultNotificationDataResponse.MatchNotificationTarget;
import com.batrobot.orchestration.application.usecase.query.GetMatchResultNotificationDataQuery;
import com.batrobot.playerstats.domain.event.PlayerMatchStatsCreatedEvent;

/**
 * Event handler for player match results.
 * Sends notification messages to all chats where the player is bound.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PlayerMatchNotificationHandler {

    private final GetMatchResultNotificationDataQuery getMatchResultNotificationDataQuery;
    private final MatchResultNotificationFormatter formatter;
    private final ApplicationEventPublisher eventPublisher;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void notifyOnMatchStatsCreated(PlayerMatchStatsCreatedEvent event) {
        log.info("Match stats created: steamId={}, matchId={}, hero={}, victory={}",
                event.steamId(),
                event.matchId(),
                event.heroName(),
                event.isVictory());

        MatchResultNotificationDataResponse notificationData =
                getMatchResultNotificationDataQuery.execute(
                        event.steamId().value(), event.matchId().value());

        if (notificationData.targets().isEmpty()) {
            log.debug("No notification targets for steamId={}", event.steamId());
            return;
        }

        for (MatchNotificationTarget target : notificationData.targets()) {
            String message = formatter.formatResult(
                    target,
                    notificationData.matchId(),
                    notificationData.startDateTime(),
                    event.heroName(),
                    event.isVictory(),
                    event.kills(),
                    event.deaths(),
                    event.assists(),
                    event.position(),
                    event.award(),
                    event.imp());
            eventPublisher.publishEvent(new OutboundMessageRequestedEvent(
                    target.telegramChatId(), null, message));
            log.debug("Sent match result notification to chat={}", target.telegramChatId());
        }
    }
}
