package com.batrobot.bot.infrastructure.telegram.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.batrobot.bot.infrastructure.telegram.event.outbound.OutboundMessageRequestedEvent;
import com.batrobot.bot.infrastructure.telegram.event.outbound.OutboundPollRequestedEvent;
import com.batrobot.bot.infrastructure.telegram.formatter.notification.UsernameChangeNotificationFormatter;
import com.batrobot.orchestration.application.dto.response.PlayerNotificationDataResponse;
import com.batrobot.orchestration.application.dto.response.PlayerNotificationDataResponse.NotificationTarget;
import com.batrobot.orchestration.application.usecase.query.GetPlayerNotificationDataQuery;
import com.batrobot.player.domain.event.PlayerUsernameUpdatedEvent;

import java.util.List;

/**
 * Event handler for Steam username changes.
 * Sends notification messages and polls to all chats where the player is bound.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PlayerUsernameNotificationHandler {

    private final GetPlayerNotificationDataQuery getPlayerNotificationDataQuery;
    private final UsernameChangeNotificationFormatter presenter;
    private final ApplicationEventPublisher eventPublisher;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void notifyOnSteamUsernameUpdated(PlayerUsernameUpdatedEvent event) {
        log.info("Steam username updated: steamId={}, oldUsername={}, newUsername={}",
                event.steamId().value(),
                event.oldSteamUsername(),
                event.newSteamUsername());

        PlayerNotificationDataResponse notificationData =
                getPlayerNotificationDataQuery.execute(event.steamId().value());

        if (notificationData.targets().isEmpty()) {
            log.debug("No notification targets for steamId={}", event.steamId());
            return;
        }

        for (NotificationTarget target : notificationData.targets()) {
            String message = presenter.formatResult(target, event.oldSteamUsername(), event.newSteamUsername());
            eventPublisher.publishEvent(new OutboundMessageRequestedEvent(
                    target.telegramChatId(), null, message));

            String pollQuestion = presenter.formatPollQuestion();
            eventPublisher.publishEvent(new OutboundPollRequestedEvent(
                    target.telegramChatId(),
                    pollQuestion,
                    List.of(event.oldSteamUsername(), event.newSteamUsername()),
                    false));

            log.debug("Sent username change notification and poll to chat={}", target.telegramChatId());
        }
    }
}

