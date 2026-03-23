package com.batrobot.ingestion.application.usecase.command;

import com.batrobot.binding.application.usecase.query.GetBoundPlayerSteamIdsQuery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Ingestion use case: sync matches for all registered players from Stratz API.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class SyncAllPlayersMatches {

    private final GetBoundPlayerSteamIdsQuery getBoundPlayerSteamIdsQuery;
    private final SyncPlayerMatches syncPlayerMatches;

    /**
     * Syncs matches for all registered players.
     * Each player sync is rate-limited individually.
     */
    public void execute() {
        log.info("Starting matches sync for all players");

        List<Long> steamIds = getBoundPlayerSteamIdsQuery.execute();

        if (steamIds.isEmpty()) {
            log.info("No bound players found for matches sync");
            return;
        }

        log.info("Found {} players to sync", steamIds.size());

        int successCount = 0;
        int errorCount = 0;

        for (Long steamId64 : steamIds) {
            try {
                syncPlayerMatches.execute(steamId64);
                successCount++;
            } catch (Exception e) {
                errorCount++;
                log.error("Failed to sync matches for player: {}", steamId64, e);
            }
        }

        log.info("Matches sync completed: {} successful, {} failed out of {} total",
                successCount, errorCount, steamIds.size());
    }
}

