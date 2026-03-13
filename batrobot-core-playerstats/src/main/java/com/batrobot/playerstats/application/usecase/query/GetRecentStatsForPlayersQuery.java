package com.batrobot.playerstats.application.usecase.query;

import com.batrobot.playerstats.application.dto.response.PlayerMatchStatsResponse;
import com.batrobot.playerstats.application.mapper.PlayerMatchStatsMapper;
import com.batrobot.playerstats.domain.repository.PlayerMatchStatsRepository;
import com.batrobot.shared.domain.model.valueobject.SteamId;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Query Use Case for fetching recent player match stats.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class GetRecentStatsForPlayersQuery {

    private final PlayerMatchStatsRepository playerMatchStatsRepository;
    private final PlayerMatchStatsMapper playerMatchStatsMapper;

    /**
     * Fetches all match stats for specified players.
     *
     * @param steamIds64 List of Steam IDs (64-bit)
     * @return List of PlayerMatchStatsResponse records
     */
    @Transactional(readOnly = true)
    public List<PlayerMatchStatsResponse> execute(@Valid @NotNull List<@NotNull Long> steamIds64) {
        log.debug("Fetching stats for {} players", steamIds64.size());

        List<SteamId> steamIdValueObjects = steamIds64.stream()
                .map(SteamId::fromSteamId64)
                .toList();

        return playerMatchStatsRepository.findStatsForPlayers(steamIdValueObjects)
                .stream()
                .map(playerMatchStatsMapper::toResponse)
                .toList();
    }
}

