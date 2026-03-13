package com.batrobot.rankhistory.infrastructure.persistence.mapper;

import com.batrobot.rankhistory.domain.model.PlayerRankHistory;
import com.batrobot.rankhistory.infrastructure.persistence.entity.PlayerRankHistoryEntity;
import com.batrobot.shared.domain.model.valueobject.SeasonRank;
import com.batrobot.shared.domain.model.valueobject.SteamId;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * Mapper between PlayerRankHistory JPA entity and domain model.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PlayerRankHistoryEntityMapper {

    /**
     * Maps from JPA entity to domain entity.
     * 
     * @param entity JPA entity
     * @return Domain entity
     */
    default PlayerRankHistory toDomain(PlayerRankHistoryEntity entity) {
        if (entity == null) {
            return null;
        }

        return PlayerRankHistory.reconstitute(
            entity.getId(),
            SteamId.fromSteamId64(entity.getSteamId()),
            SeasonRank.of(entity.getSeasonRank()),
            entity.getAssignedAt()
        );
    }

    /**
     * Maps from domain entity to JPA entity.
     * 
     * @param domain Domain entity
     * @return JPA entity
     */
    @Mapping(target = "steamId", source = "steamId.value")
    @Mapping(target = "seasonRank", source = "seasonRank.value")
    PlayerRankHistoryEntity toEntity(PlayerRankHistory domain);
}


