package com.batrobot.player.application.usecase.command;

import com.batrobot.player.application.dto.request.PlayerRequest;
import com.batrobot.player.application.dto.response.PlayerResponse;
import com.batrobot.player.application.mapper.PlayerMapper;
import com.batrobot.player.domain.repository.PlayerRepository;
import com.batrobot.shared.domain.model.valueobject.SeasonRank;
import com.batrobot.shared.domain.model.valueobject.SteamId;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * UseCase for upserting a player.
 * Creates new player if not exists, updates if info changed.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class UpsertPlayer {

    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;

    /**
     * Upserts a player - creates if not exists, updates if info changed.
     * Single database operation using repository.
     * 
     * @param request Player request with Steam data
     * @return Player response
     */
    @Transactional
    public PlayerResponse execute(@Valid PlayerRequest request) {
        log.debug("Upserting player: steamId={}", request.getSteamId64());

        SteamId steamId = SteamId.fromSteamId64(request.getSteamId64());

        return playerRepository.findBySteamId(steamId)
                .map(existing -> {
                    // Player exists, check if info needs updating
                    boolean hasChanges = false;

                    hasChanges |= existing.updateProfile(
                            request.getSteamUsername(),
                            request.getProfileUrl(),
                            request.getAvatarUrl(),
                            request.getAccountCreationDate());

                    hasChanges |= existing.updateVisibilitySettings(
                            request.getCommunityVisibleState(),
                            request.getIsAnonymous(),
                            request.getIsStratzPublic());

                    hasChanges |= existing.updateIsDotaPlusSubscriber(request.getIsDotaPlusSubscriber());

                    hasChanges |= existing.updateSmurfFlag(request.getSmurfFlag());

                    hasChanges |= existing.updateRankInfo(
                            SeasonRank.of(request.getSeasonRank()),
                            request.getImp(),
                            request.getActivity());

                    hasChanges |= existing.updateMatchStatistics(
                            request.getMatchCount(),
                            request.getWinCount(),
                            request.getFirstMatchDate(),
                            request.getLastMatchDate());

                    if (hasChanges) {
                        playerRepository.save(existing);
                        log.debug("Updated player info for steam ID {}", steamId.value());
                    }

                    return playerMapper.toResponse(existing);
                })
                .orElseGet(() -> {
                    log.debug("Creating new player: steamId={}", steamId.value());
                    var newPlayer = playerMapper.createFromRequest(request);
                    var savedPlayer = playerRepository.save(newPlayer);

                    return playerMapper.toResponse(savedPlayer);
                });
    }
}

