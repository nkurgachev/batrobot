package com.batrobot.bot.infrastructure.telegram.scheduler;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.batrobot.bot.infrastructure.telegram.event.outbound.OutboundMessageRequestedEvent;
import com.batrobot.bot.infrastructure.telegram.formatter.notification.DailyChatMessageStatsFormatter;
import com.batrobot.bot.infrastructure.telegram.notification.DailyChatMessageStatsTracker;
import com.batrobot.bot.infrastructure.telegram.notification.DailyChatMessageStatsTracker.ChatMessageStatsSnapshot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Publishes daily per-chat non-command message statistics at midnight.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DailyChatMessageStatsScheduler {

    private final DailyChatMessageStatsTracker tracker;
    private final DailyChatMessageStatsFormatter formatter;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(cron = "${telegram.notification.scheduler.daily-message-stats-cron:0 0 0 * * *}",
            zone = "${app.day.timezone:Europe/Moscow}")
    public void publishDailyStats() {
        List<ChatMessageStatsSnapshot> snapshots = tracker.drainAll();
        if (snapshots.isEmpty()) {
            log.debug("Skipping daily chat message stats notification: no non-command messages collected");
            return;
        }

        for (ChatMessageStatsSnapshot snapshot : snapshots) {
            String text = formatter.format(snapshot.messageCounts(), snapshot.languageCode());
            eventPublisher.publishEvent(new OutboundMessageRequestedEvent(snapshot.chatId(), null, text));

            log.info("Published daily non-command message stats for chat {} with {} users",
                    snapshot.chatId(), snapshot.messageCounts().size());
        }
    }
}