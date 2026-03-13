package com.batrobot.player.application.usecase.query;

import com.batrobot.player.application.dto.response.PlayerResponse;
import com.batrobot.player.application.mapper.PlayerMapper;
import com.batrobot.player.domain.model.Player;
import com.batrobot.player.domain.repository.PlayerRepository;
import com.batrobot.shared.domain.model.valueobject.SteamId;

import jakarta.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;

/**
 * UseCase for getting players by external Steam IDs.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class GetPlayersBySteamIdsQuery {

    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;

    /**
     * Gets players by their Steam IDs (business keys).
     * 
     * @param steamIds List of Steam ID values (64-bit format)
     * @return List of player responses
     */
    @Transactional(readOnly = true)
    public List<PlayerResponse> execute(@NotNull List<Long> steamIds) {
        if (steamIds == null || steamIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<SteamId> steamIdValueObjects = steamIds.stream()
            .distinct()
            .map(SteamId::fromSteamId64)
            .toList();

        List<Player> players = playerRepository.findAllBySteamIds(steamIdValueObjects);
        log.debug("Found {} players for {} steam IDs", players.size(), steamIdValueObjects.size());

        return players.stream()
            .map(playerMapper::toResponse)
            .toList();
    }
}

