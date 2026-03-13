package com.batrobot.rankhistory.application.usecase.query;

import com.batrobot.rankhistory.application.dto.response.PlayerRankHistoryResponse;
import com.batrobot.rankhistory.application.mapper.PlayerRankHistoryMapper;
import com.batrobot.rankhistory.domain.model.PlayerRankHistory;
import com.batrobot.rankhistory.domain.repository.PlayerRankHistoryRepository;
import com.batrobot.shared.domain.model.valueobject.SteamId;

import jakarta.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Query Use Case for fetching rank history for multiple players.
 * 
 * Returns rank history records ordered by steamId and assignedAt.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class GetRankHistoryForPlayersQuery {

    private final PlayerRankHistoryRepository rankHistoryRepository;
    private final PlayerRankHistoryMapper playerRankHistoryMapper;

    /**
     * Fetches rank history for multiple Steam IDs.
     * 
     * @param steamIds List of Steam ID values (64-bit format)
     * @return List of player rank history responses ordered by steamId
     */
    @Transactional(readOnly = true)
    public List<PlayerRankHistoryResponse> execute(@NotNull List<Long> steamIds) {
        if (steamIds == null || steamIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<SteamId> steamIdValueObjects = steamIds.stream()
            .distinct()
            .map(SteamId::fromSteamId64)
            .toList();

        List<PlayerRankHistory> histories = rankHistoryRepository
            .findAllBySteamIdInOrderBySteamIdAscAssignedAtAsc(steamIdValueObjects);

        log.debug("Found {} rank history records for {} steam IDs", histories.size(), steamIdValueObjects.size());
        
        Map<Long, List<PlayerRankHistory>> historyBySteamId = new LinkedHashMap<>();
        for (PlayerRankHistory history : histories) {
            Long steamId64 = history.getSteamId().value();
            historyBySteamId
                .computeIfAbsent(steamId64, key -> new ArrayList<>())
                .add(history);
        }

        List<PlayerRankHistoryResponse> responses = new ArrayList<>();
        for (Map.Entry<Long, List<PlayerRankHistory>> entry : historyBySteamId.entrySet()) {
            responses.add(playerRankHistoryMapper.toHistoryResponse(entry.getValue(), entry.getKey()));
        }

        return responses;
    }
}

