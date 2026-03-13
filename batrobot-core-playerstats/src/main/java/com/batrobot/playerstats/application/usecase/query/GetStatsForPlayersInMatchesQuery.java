package com.batrobot.playerstats.application.usecase.query;

import com.batrobot.playerstats.application.dto.response.PlayerMatchStatsResponse;
import com.batrobot.playerstats.application.mapper.PlayerMatchStatsMapper;
import com.batrobot.playerstats.domain.repository.PlayerMatchStatsRepository;
import com.batrobot.shared.domain.model.valueobject.MatchId;
import com.batrobot.shared.domain.model.valueobject.SteamId;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;

/**
 * Query Use Case for fetching player match stats filtered by both match IDs and player IDs.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class GetStatsForPlayersInMatchesQuery {

    private final PlayerMatchStatsRepository playerMatchStatsRepository;
    private final PlayerMatchStatsMapper playerMatchStatsMapper;

    /**
     * Fetches stats for specified players in specified matches.
     *
     * @param matchIds  Collection of match IDs (Dota 2 match IDs)
     * @param steamIds64 List of Steam IDs (64-bit)
     * @return List of PlayerMatchStatsResponse
     */
    @Transactional(readOnly = true)
    public List<PlayerMatchStatsResponse> execute(
            @Valid @NotNull Collection<@NotNull Long> matchIds,
            @Valid @NotNull List<@NotNull Long> steamIds64) {
        log.debug("Fetching stats for {} players in {} matches", steamIds64.size(), matchIds.size());

        List<MatchId> matchIdObjects = matchIds.stream().map(MatchId::of).toList();
        List<SteamId> steamIdObjects = steamIds64.stream().map(SteamId::fromSteamId64).toList();

        return playerMatchStatsRepository.findByMatchIdsAndSteamIds(matchIdObjects, steamIdObjects)
                .stream()
                .map(playerMatchStatsMapper::toResponse)
                .toList();
    }
}
