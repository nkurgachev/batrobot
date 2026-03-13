package com.batrobot.match.application.mapper;

import com.batrobot.match.application.dto.request.MatchRequest;
import com.batrobot.match.application.dto.response.MatchResponse;
import com.batrobot.match.domain.model.Match;
import com.batrobot.shared.domain.model.valueobject.MatchId;

import org.mapstruct.*;

/**
 * Application-layer mapper for match-related conversions.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MatchMapper {

    /**
     * Creates a new Match domain entity from MatchRequest.
     */
    default Match createFromRequest(MatchRequest request) {
        return Match.create(
                MatchId.of(request.getMatchId()),
                request.getDurationSeconds(),
                request.getStartDateTime(),
                request.getEndDateTime(),
                request.getLobbyType(),
                request.getGameMode(),
                request.getActualRank(),
                request.getRadiantKills(),
                request.getDireKills(),
                request.getAnalysisOutcome(),
                request.getBottomLaneOutcome(),
                request.getMidLaneOutcome(),
                request.getTopLaneOutcome());
    }

    /**
     * Converts Domain Match entity to MatchResponse record.
     */
    @Mapping(target = "matchId", expression = "java(match.getMatchId().value())")
    MatchResponse toResponse(Match match);
}

