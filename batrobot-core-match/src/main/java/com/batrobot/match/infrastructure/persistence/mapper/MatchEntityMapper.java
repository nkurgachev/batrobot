package com.batrobot.match.infrastructure.persistence.mapper;

import com.batrobot.match.domain.model.Match;
import com.batrobot.match.infrastructure.persistence.entity.MatchEntity;
import com.batrobot.shared.domain.model.valueobject.MatchId;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * Mapper for converting between JPA Match entity and Domain Match.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MatchEntityMapper {

    /**
     * Maps from JPA entity to domain entity.
     * 
     * @param entity JPA entity
     * @return Domain entity
     */
    default Match toDomain(MatchEntity entity) {
        MatchId matchId = MatchId.of(entity.getMatchId());

        return Match.reconstitute(
            entity.getId(),
            matchId,
            entity.getDurationSeconds(),
            entity.getStartDateTime(),
            entity.getEndDateTime(),
            entity.getLobbyType(),
            entity.getGameMode(),
            entity.getActualRank(),
            entity.getRadiantKills(),
            entity.getDireKills(),
            entity.getAnalysisOutcome(),
            entity.getBottomLaneOutcome(),
            entity.getMidLaneOutcome(),
            entity.getTopLaneOutcome(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    /**
     * Maps from domain entity to JPA entity.
     * 
     * @param domain Domain entity
     * @return JPA entity
     */
    @Mapping(target = "matchId", source = "matchId.value")
    MatchEntity toEntity(Match domain);
}

