package com.batrobot.playerstats.application.usecase.query;

import com.batrobot.playerstats.domain.repository.PlayerMatchStatsRepository;
import com.batrobot.shared.domain.model.valueobject.SteamId;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * Query Use Case for checking if a player has any match history.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class HasPlayerMatchHistoryQuery {

    private final PlayerMatchStatsRepository playerMatchStatsRepository;

    /**
     * Checks if a player has any match stats records.
     *
     * @param steamId64 Player's Steam ID (64-bit)
     * @return true if the player has at least one match stats record
     */
    @Transactional(readOnly = true)
    public boolean execute(@Valid @NotNull Long steamId64) {
        SteamId steamId = SteamId.fromSteamId64(steamId64);
        boolean hasHistory = playerMatchStatsRepository.findLatestStatsBySteamId(steamId).isPresent();
        log.trace("Player {} has match history: {}", steamId64, hasHistory);
        return hasHistory;
    }
}

