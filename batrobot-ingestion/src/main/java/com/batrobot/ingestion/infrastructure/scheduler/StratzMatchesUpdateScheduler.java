package com.batrobot.ingestion.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.batrobot.ingestion.application.usecase.command.SyncAllPlayersMatches;

/**
 * Scheduler for periodic match data synchronization from Stratz API.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StratzMatchesUpdateScheduler {

    private final SyncAllPlayersMatches syncAllPlayersMatches;

    @Scheduled(cron = "${ingestion.stratz.scheduler.match-update-cron:0 */5 * * * ?}")
    public void updatePlayerMatches() {
        log.info("Triggering scheduled Stratz match data update");
        syncAllPlayersMatches.execute();
    }
}
