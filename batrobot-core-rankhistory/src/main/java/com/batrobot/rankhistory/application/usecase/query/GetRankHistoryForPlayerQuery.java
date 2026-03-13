package com.batrobot.rankhistory.application.usecase.query;

import com.batrobot.rankhistory.application.dto.response.PlayerRankHistoryResponse;
import com.batrobot.rankhistory.application.mapper.PlayerRankHistoryMapper;
import com.batrobot.rankhistory.domain.repository.PlayerRankHistoryRepository;
import com.batrobot.shared.domain.model.valueobject.SteamId;

import jakarta.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

/**
 * UseCase for getting player rank history by Steam ID.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class GetRankHistoryForPlayerQuery {
    
    private final PlayerRankHistoryRepository rankHistoryRepository;
    private final PlayerRankHistoryMapper rankMapper;
    
    /**
     * Executes the query to get player rank history by Steam ID.
     * Retrieves all rank records for the player, pre-sorted by database query.
     * 
     * @param steamId64 Steam account ID (64-bit format)
     * @return Player rank history response containing all seasonal ranks
     */
    @Transactional(readOnly = true)
    public Optional<PlayerRankHistoryResponse> execute(@NotNull Long steamId64) {
        log.debug("Getting rank history for Steam ID: {}", steamId64);

        return Optional.of(rankHistoryRepository.findAllBySteamIdOrderByAssignedAtAsc(
                SteamId.fromSteamId64(steamId64)
            ))
            .filter(history -> !history.isEmpty())
            .map(history -> {
                log.debug("Found {} rank history records for Steam ID: {}", history.size(), steamId64);
                return rankMapper.toHistoryResponse(history, steamId64);
            });
    }
}

