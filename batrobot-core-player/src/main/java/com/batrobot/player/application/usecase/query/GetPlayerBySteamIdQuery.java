package com.batrobot.player.application.usecase.query;

import com.batrobot.player.application.dto.response.PlayerResponse;
import com.batrobot.player.application.mapper.PlayerMapper;
import com.batrobot.player.domain.repository.PlayerRepository;
import com.batrobot.shared.domain.model.valueobject.SteamId;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

/**
 * UseCase for getting a player by external Steam ID.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class GetPlayerBySteamIdQuery {

    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;

    /**
     * Gets a player by their Steam ID (business key).
     * 
     * @param steamId64 Steam ID value (64-bit format)
     * @return Optional with player response if found, empty otherwise
     */
    @Transactional(readOnly = true)
    public Optional<PlayerResponse> execute(@NotNull Long steamId64) {
        log.debug("Getting player by Steam ID: {}", steamId64);

        return playerRepository.findBySteamId(SteamId.fromSteamId64(steamId64))
            .map(player -> {
                log.debug("Found player: steamId={}", steamId64);
                return playerMapper.toResponse(player);
            });
    }
}

