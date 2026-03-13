package com.batrobot.ingestion.application.mapper;

import com.batrobot.stratz.application.dto.response.StratzMatchResponse;
import com.batrobot.stratz.application.dto.response.StratzPlayerMatchStatsResponse;
import com.batrobot.stratz.application.dto.response.StratzPlayerResponse;
import com.batrobot.match.application.dto.request.MatchRequest;
import com.batrobot.player.application.dto.request.PlayerRequest;
import com.batrobot.playerstats.application.dto.request.PlayerMatchStatsRequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * Maps Stratz intermediate DTOs to core request DTOs used by ingestion use cases.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IngestionRequestMapper {

    @Mapping(target = "accountCreationDate", source = "timeCreated")
    PlayerRequest toPlayerRequest(StratzPlayerResponse dto);

    MatchRequest toMatchRequest(StratzMatchResponse dto);

    PlayerMatchStatsRequest toPlayerMatchStatsRequest(StratzPlayerMatchStatsResponse dto, Long matchId, Long steamId64);
}


