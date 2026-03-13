package com.batrobot.steam.application.usecase.query;

import com.batrobot.steam.application.dto.response.SteamPlayerResponse;
import com.batrobot.steam.application.exception.SteamUnavailableException;
import com.batrobot.steam.application.port.SteamApiPort;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Domain Query Use Case for fetching Steam players currently in-game.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class GetSteamPlayersInGameQuery {

    private final SteamApiPort steamApiPort;

    /**
     * Fetches player summaries for given Steam IDs and returns only those currently in-game.
     *
     * @param steamIds64 List of Steam IDs (64-bit) to check
     * @return List of Steam player DTOs for players currently in-game
     * @throws SteamUnavailableException if Steam API communication fails
     */
    public List<SteamPlayerResponse> execute(@NotNull List<Long> steamIds64) {
        if (steamIds64.isEmpty()) {
            log.debug("Empty steam IDs list, returning empty result");
            return List.of();
        }

        log.debug("Fetching in-game status for {} Steam IDs", steamIds64.size());

        Map<Long, SteamPlayerResponse> playersMap = steamApiPort.getPlayerSummariesBulk(steamIds64);

        List<SteamPlayerResponse> playersInGame = playersMap.values().stream()
                .filter(this::isPlayerInGame)
                .toList();

        log.debug("Found {} players in-game out of {} requested", playersInGame.size(), steamIds64.size());
        return playersInGame;
    }

    private boolean isPlayerInGame(SteamPlayerResponse player) {
        return player.gameName() != null && !player.gameName().isBlank();
    }
}

