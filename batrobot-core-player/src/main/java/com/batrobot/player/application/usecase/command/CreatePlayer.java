package com.batrobot.player.application.usecase.command;

import com.batrobot.player.application.dto.request.PlayerRequest;
import com.batrobot.player.application.dto.response.PlayerResponse;
import com.batrobot.player.application.mapper.PlayerMapper;
import com.batrobot.player.domain.exception.PlayerAlreadyExistsException;
import com.batrobot.player.domain.model.Player;
import com.batrobot.player.domain.repository.PlayerRepository;
import com.batrobot.player.domain.specification.UniquePlayerSpecification;
import com.batrobot.player.domain.specification.UniquePlayerSpecification.PlayerContext;
import com.batrobot.shared.domain.model.valueobject.SteamId;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * UseCase for creating a new player.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class CreatePlayer {

    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;
    private final UniquePlayerSpecification uniquePlayerSpecification;

    /**
     * Creates a new player.
     * 
     * @param request Player data
     * @return Created player response
     * @throws PlayerAlreadyExistsException if player with this Steam ID already exists
     */
    @Transactional
    public PlayerResponse execute(@Valid PlayerRequest request)
            throws PlayerAlreadyExistsException {
        log.debug("Creating new player: steamId={}", request.getSteamId64());

        SteamId steamId = SteamId.fromSteamId64(request.getSteamId64());

        uniquePlayerSpecification.check(
                PlayerContext.builder()
                        .steamId(steamId)
                        .build());

        Player newPlayer = playerMapper.createFromRequest(request);
        Player savedPlayer = playerRepository.save(newPlayer);

        log.info("Successfully created new player: {}", steamId.value());

        return playerMapper.toResponse(savedPlayer);
    }
}

