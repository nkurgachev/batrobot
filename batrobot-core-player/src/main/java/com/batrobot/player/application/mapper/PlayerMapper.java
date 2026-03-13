package com.batrobot.player.application.mapper;

import com.batrobot.player.application.dto.request.PlayerRequest;
import com.batrobot.player.application.dto.response.PlayerResponse;
import com.batrobot.player.domain.model.Player;
import com.batrobot.shared.domain.model.valueobject.SeasonRank;
import com.batrobot.shared.domain.model.valueobject.SteamId;

import org.mapstruct.*;

/**
 * MapStruct mapper for converting between Domain Player and DTOs.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PlayerMapper {

    /**
     * Creates a new Player domain entity from request.
     * 
     * @param request Player request with Steam data
     * @return New Player domain entity
     */
    default Player createFromRequest(PlayerRequest request) {
        return Player.create(
                SteamId.fromSteamId64(request.getSteamId64()),
                request.getSteamUsername(),
                request.getProfileUrl(),
                request.getAvatarUrl(),
                request.getAccountCreationDate(),
                request.getCommunityVisibleState(),
                request.getIsAnonymous(),
                request.getIsStratzPublic(),
                request.getIsDotaPlusSubscriber(),
                request.getSmurfFlag(),
                request.getSeasonRank() != null
                        ? SeasonRank.of(request.getSeasonRank())
                        : null,
                request.getActivity(),
                request.getImp(),
                request.getMatchCount(),
                request.getWinCount(),
                request.getFirstMatchDate(),
                request.getLastMatchDate());
    }

    /**
     * Converts Domain Player entity to PlayerResponse.
     * 
     * @param player Domain entity
     * @return Application Response DTO
     */
    @Mapping(target = "steamId64", source = "steamId.value")
    @Mapping(target = "seasonRank", source = "seasonRank.value")
    PlayerResponse toResponse(Player player);
}

