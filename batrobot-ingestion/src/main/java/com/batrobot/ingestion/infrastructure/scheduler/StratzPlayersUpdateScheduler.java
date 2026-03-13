package com.batrobot.ingestion.infrastructure.scheduler;

import com.batrobot.ingestion.application.usecase.command.SyncAllPlayers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Scheduler for periodic player profile synchronization from Stratz API.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StratzPlayersUpdateScheduler {

    private final SyncAllPlayers syncAllPlayers;

    @Scheduled(cron = "${ingestion.stratz.scheduler.player-update-cron:0 0 * * * ?}")
    public void updatePlayerProfiles() {
        log.info("Triggering scheduled Stratz player data update");
        syncAllPlayers.execute();
    }
}
