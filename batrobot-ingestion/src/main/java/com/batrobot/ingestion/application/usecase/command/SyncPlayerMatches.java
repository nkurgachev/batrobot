package com.batrobot.ingestion.application.usecase.command;

import com.batrobot.stratz.application.dto.response.StratzMatchResponse;
import com.batrobot.stratz.application.dto.response.StratzMatchesResponse;
import com.batrobot.stratz.application.usecase.query.GetMatchesForPlayerQuery;

import com.batrobot.match.application.dto.request.MatchRequest;
import com.batrobot.match.application.usecase.command.UpsertMatch;
import com.batrobot.match.application.dto.response.MatchResponse;
import com.batrobot.match.application.usecase.query.GetMatchesByMatchIdsQuery;
import com.batrobot.playerstats.application.dto.request.PlayerMatchStatsRequest;
import com.batrobot.playerstats.application.dto.response.PlayerMatchStatsResponse;
import com.batrobot.playerstats.application.usecase.command.UpsertPlayerMatchStats;
import com.batrobot.playerstats.application.usecase.query.GetLatestPlayerStatsQuery;

import com.batrobot.ingestion.application.mapper.IngestionRequestMapper;
import com.batrobot.ingestion.application.port.config.IngestionStratzSyncConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Ingestion use case: sync matches for a single player from Stratz API.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class SyncPlayerMatches {

    private final GetMatchesForPlayerQuery getMatchesForPlayer;
    private final UpsertMatch upsertMatch;
    private final UpsertPlayerMatchStats upsertPlayerMatchStats;
    private final GetLatestPlayerStatsQuery getLatestPlayerStatsQuery;
    private final GetMatchesByMatchIdsQuery getMatchesByMatchIdsQuery;
    private final IngestionStratzSyncConfig stratzSyncConfig;
    private final IngestionRequestMapper requestMapper;

    /**
     * Syncs matches for a single player from Stratz API.
     *
     * @param steamId64 Player's Steam ID (64-bit)
     */
    public void execute(Long steamId64) {
        log.debug("Syncing matches from Stratz for player: {}", steamId64);

        int take = stratzSyncConfig.getMatchesLimit();
        long startTime;

        startTime = getLatestPlayerStatsQuery.execute(steamId64)
                .map(PlayerMatchStatsResponse::matchId)
                .map(matchId -> getMatchesByMatchIdsQuery.execute(List.of(matchId)))
                .flatMap(matches -> matches.values().stream().findFirst())
                .map(MatchResponse::endDateTime)
                .orElse(stratzSyncConfig.getHistoricalStartTimestamp());

        log.debug("Player {}: startTime={}, take={}", steamId64, startTime, take);

        StratzMatchesResponse response = getMatchesForPlayer.execute(steamId64, startTime, take);

        if (response.getMatches().isEmpty()) {
            log.debug("No matches found for player: {}", steamId64);
            return;
        }

        log.debug("Fetched {} matches for player {}", response.getMatches().size(), steamId64);

        for (StratzMatchResponse stratzMatch : response.getMatches()) {
            try {
                processMatch(stratzMatch, steamId64);
            } catch (Exception e) {
                log.error("Failed to process match {} for player {}",
                        stratzMatch.getMatchId(), steamId64, e);
            }
        }

        log.debug("Completed match sync for player {}", steamId64);
    }

    private void processMatch(StratzMatchResponse stratzMatch, Long steamId64) {
        MatchRequest matchRequest = requestMapper.toMatchRequest(stratzMatch);
        upsertMatch.execute(matchRequest);

        if (stratzMatch.getPlayerStats() != null) {
            PlayerMatchStatsRequest statsRequest = requestMapper.toPlayerMatchStatsRequest(
                    stratzMatch.getPlayerStats(),
                    stratzMatch.getMatchId(),
                    steamId64);
            upsertPlayerMatchStats.execute(statsRequest);
        }
    }
}


