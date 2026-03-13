package com.batrobot.playerstats.application.mapper;

import com.batrobot.shared.domain.model.valueobject.SteamId;
import com.batrobot.playerstats.application.dto.request.PlayerMatchStatsRequest;
import com.batrobot.playerstats.application.dto.response.PlayerMatchStatsResponse;
import com.batrobot.playerstats.domain.model.PlayerMatchStats;
import com.batrobot.shared.domain.model.valueobject.MatchId;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * MapStruct mapper for converting between Domain PlayerMatchStats and DTOs.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PlayerMatchStatsMapper {

    // ==================== Domain → Response record ====================

    /**
     * Converts Domain PlayerMatchStats entity to PlayerMatchStatsResponse record.
     */
    @Mapping(target = "matchId", expression = "java(stats.getMatchId().value())")
    @Mapping(target = "steamId64", expression = "java(stats.getSteamId().value())")
    @Mapping(target = "kills", source = "kda.kills")
    @Mapping(target = "deaths", source = "kda.deaths")
    @Mapping(target = "assists", source = "kda.assists")
    @Mapping(target = "numLastHits", source = "economy.numLastHits")
    @Mapping(target = "numDenies", source = "economy.numDenies")
    @Mapping(target = "goldPerMinute", source = "economy.goldPerMinute")
    @Mapping(target = "experiencePerMinute", source = "economy.experiencePerMinute")
    @Mapping(target = "heroDamage", source = "combat.heroDamage")
    @Mapping(target = "towerDamage", source = "combat.towerDamage")
    @Mapping(target = "heroHealing", source = "combat.heroHealing")
    PlayerMatchStatsResponse toResponse(PlayerMatchStats stats);

    // ==================== Request → Domain (factory) ====================

    /**
     * Creates a new PlayerMatchStats domain entity from request DTO.
     */
    default PlayerMatchStats createFromRequest(PlayerMatchStatsRequest request) {
        return PlayerMatchStats.create(
                MatchId.of(request.getMatchId()),
                SteamId.fromSteamId64(request.getSteamId64()),
                request.getHeroId(),
                request.getHeroName(),
                request.getIsVictory(),
                request.getIsRadiant(),
                request.getKills(),
                request.getDeaths(),
                request.getAssists(),
                request.getNumLastHits(),
                request.getNumDenies(),
                request.getGoldPerMinute(),
                request.getExperiencePerMinute(),
                request.getHeroDamage(),
                request.getTowerDamage(),
                request.getHeroHealing(),
                request.getLane(),
                request.getPosition(),
                request.getImp(),
                request.getAward(),
                request.getCampStack(),
                request.getCourierKills(),
                request.getSentryWardsPurchased(),
                request.getObserverWardsPurchased(),
                request.getSentryWardsDestroyed(),
                request.getObserverWardsDestroyed());
    }
}

