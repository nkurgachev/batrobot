package com.batrobot.ingestion.application.usecase.command;

import com.batrobot.stratz.application.dto.response.StratzPlayerResponse;
import com.batrobot.stratz.application.usecase.query.GetPlayersFromStratzQuery;

import com.batrobot.player.application.dto.request.PlayerRequest;
import com.batrobot.player.application.usecase.command.UpsertPlayer;
import com.batrobot.player.application.usecase.query.GetAllPlayerSteamIdsQuery;

import com.batrobot.ingestion.application.mapper.IngestionRequestMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Ingestion use case: sync player profiles for all registered players from Stratz API.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class SyncAllPlayers {

    private final GetAllPlayerSteamIdsQuery getAllPlayerSteamIdsQuery;
    private final GetPlayersFromStratzQuery getPlayersFromStratzQuery;
    private final UpsertPlayer upsertPlayer;
    private final IngestionRequestMapper requestMapper;

    /**
     * Loads all registered players from Stratz and updates Player aggregate data.
     */
    public void execute() {
        log.info("Starting player profiles sync for all players");

        List<Long> steamIds = getAllPlayerSteamIdsQuery.execute();
        if (steamIds.isEmpty()) {
            log.info("No players found for profile sync");
            return;
        }

        log.info("Found {} players to sync profiles", steamIds.size());

        List<StratzPlayerResponse> playersFromStratz = getPlayersFromStratzQuery.execute(steamIds);
        if (playersFromStratz.isEmpty()) {
            log.info("Stratz returned no player profiles for sync");
            return;
        }

        int successCount = 0;
        int errorCount = 0;

        for (StratzPlayerResponse stratzPlayer : playersFromStratz) {
            try {
                PlayerRequest playerRequest = requestMapper.toPlayerRequest(stratzPlayer);
                upsertPlayer.execute(playerRequest);
                successCount++;
            } catch (Exception e) {
                errorCount++;
                log.error("Failed to sync profile for player: {}", stratzPlayer.getSteamId64(), e);
            }
        }

        log.info("Player profiles sync completed: {} successful, {} failed out of {} received",
                successCount, errorCount, playersFromStratz.size());
    }
}


