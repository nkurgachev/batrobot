package com.batrobot.steam.application.mapper;

import org.mapstruct.*;

import com.batrobot.steam.application.dto.response.SteamPlayerResponse;

/**
 * Mapper for in-game player DTOs.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SteamMapper {

    /**
     * Converts Steam API Player to PlayerInGameResponse.
     * Game status fields are set manually from additional properties in the adapter.
     */
    @Mapping(target = "steamId64", source = "steamid")
    @Mapping(target = "steamUsername", source = "personaname")
    @Mapping(target = "gameName", expression = "java(player.getAdditionalProperties().get(\"gameextrainfo\") instanceof String ? (String) player.getAdditionalProperties().get(\"gameextrainfo\") : null)")
    @Mapping(target = "gameId", expression = "java(player.getAdditionalProperties().get(\"gameid\") != null ? Long.valueOf((String) player.getAdditionalProperties().get(\"gameid\")) : null)")
    SteamPlayerResponse toResponse(com.lukaspradel.steamapi.data.json.playersummaries.Player player);
}

