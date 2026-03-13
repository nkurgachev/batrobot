package com.batrobot.playerstats.application.usecase.query;

import com.batrobot.playerstats.application.dto.response.PlayerMatchStatsResponse;
import com.batrobot.playerstats.application.mapper.PlayerMatchStatsMapper;
import com.batrobot.playerstats.domain.repository.PlayerMatchStatsRepository;
import com.batrobot.shared.domain.model.valueobject.SteamId;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

/**
 * Query Use Case for fetching latest player match stats by Steam ID.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class GetLatestPlayerStatsQuery {

    private final PlayerMatchStatsRepository playerMatchStatsRepository;
    private final PlayerMatchStatsMapper playerMatchStatsMapper;

    /**
     * Returns the latest match stats for a player, if any exist.
     *
     * @param steamId64 Player's Steam ID (64-bit)
     * @return Optional with latest player match stats, empty if no history
     */
    @Transactional(readOnly = true)
    public Optional<PlayerMatchStatsResponse> execute(@NotNull Long steamId64) {
        log.debug("Fetching latest stats for player {}", steamId64);

        return playerMatchStatsRepository
                .findLatestStatsBySteamId(SteamId.fromSteamId64(steamId64))
                .map(playerMatchStatsMapper::toResponse);
    }
}
