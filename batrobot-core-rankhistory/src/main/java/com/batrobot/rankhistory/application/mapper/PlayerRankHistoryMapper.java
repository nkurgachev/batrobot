package com.batrobot.rankhistory.application.mapper;

import com.batrobot.rankhistory.application.dto.request.PlayerRankRequest;
import com.batrobot.rankhistory.application.dto.response.PlayerRankHistoryResponse;
import com.batrobot.rankhistory.application.dto.response.PlayerRankResponse;
import com.batrobot.rankhistory.application.dto.response.PlayerRankHistoryResponse.Rank;
import com.batrobot.rankhistory.domain.model.PlayerRankHistory;
import com.batrobot.shared.domain.model.valueobject.SeasonRank;
import com.batrobot.shared.domain.model.valueobject.SteamId;

import java.util.List;

import org.mapstruct.*;

/**
 * MapStruct mapper for converting between Domain Player and DTOs.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PlayerRankHistoryMapper {
    
    /**
     * Creates a new PlayerRankHistory domain entity from request.
     * 
     * @param request PlayerRankRequest with steam ID and season rank
     * @return New PlayerRankHistory domain entity
     */
    default PlayerRankHistory createFromRequest(PlayerRankRequest request) {
        return PlayerRankHistory.create(
            SteamId.fromSteamId64(request.getSteamId64()),
            SeasonRank.of(request.getSeasonRank())
        );
    }

    /**
     * Converts Domain PlayerRankHistory entity to PlayerRankResponse.
     * 
     * @param history Domain entity
     * @return PlayerRankResponse DTO
     */
    @Mapping(target = "steamId64", source = "steamId.value")
    @Mapping(target = "seasonRank", source = "seasonRank.value")
    PlayerRankResponse toResponse(PlayerRankHistory history);

    /**
     * Converts Domain PlayerRankHistory entity list to PlayerRankHistoryResponse.
     * Groups all rank history for a single Steam account.
     * 
     * @param histories List of domain PlayerRankHistory entities (already sorted by assignedAt)
     * @param steamId64 Steam account ID
     * @return PlayerRankHistoryResponse with aggregated rank history
     */
    default PlayerRankHistoryResponse toHistoryResponse(List<PlayerRankHistory> histories, Long steamId64) {
        List<Rank> ranks = histories.stream()
            .map(history -> new Rank(
                history.getId(),
                history.getSeasonRank().value(),
                history.getAssignedAt()
            ))
            .toList();
        return new PlayerRankHistoryResponse(steamId64, ranks);
    }
}

