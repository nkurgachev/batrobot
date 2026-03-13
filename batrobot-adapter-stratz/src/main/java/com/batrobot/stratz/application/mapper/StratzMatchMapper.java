package com.batrobot.stratz.application.mapper;

import com.batrobot.stratz.application.dto.response.StratzMatchResponse;
import com.batrobot.stratz.application.dto.response.StratzMatchesResponse;
import com.batrobot.stratz.application.dto.response.StratzPlayerMatchStatsResponse;

import com.batrobot.stratz.generated.types.MatchPlayerType;
import com.batrobot.stratz.generated.types.MatchType;
import com.batrobot.stratz.generated.types.PlayerType;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Infrastructure mapper: Stratz GraphQL generated types → intermediate DTOs.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StratzMatchMapper {

    /**
     * Maps PlayerType to StratzMatchesResponse.
     *
     * @param player Stratz PlayerType from GraphQL response
     * @param steamId64 Player's 64-bit Steam ID
     * @return StratzMatchesResponse with mapped matches
     */
    default StratzMatchesResponse toStratzMatchesResponse(PlayerType player, Long steamId64) {
        StratzMatchesResponse response = new StratzMatchesResponse();
        response.setSteamId64(steamId64);

        if (player == null || player.getMatches() == null || player.getMatches().isEmpty()) {
            response.setMatches(List.of());
            return response;
        }

        List<StratzMatchResponse> matches = player.getMatches().stream()
                .filter(match -> match.getPlayers() != null && !match.getPlayers().isEmpty())
                .map(match -> toStratzMatchResponse(match, match.getPlayers().get(0)))
                .collect(Collectors.toList());

        response.setMatches(matches);
        return response;
    }
    
    // ==================== Match mapping ====================

    @Mapping(target = "matchId", source = "match.id")
    @Mapping(target = "durationSeconds", source = "match.durationSeconds")
    @Mapping(target = "startDateTime", source = "match.startDateTime")
    @Mapping(target = "endDateTime", source = "match.endDateTime")
    @Mapping(target = "lobbyType", source = "match.lobbyType", qualifiedByName = "enumToName")
    @Mapping(target = "gameMode", source = "match.gameMode", qualifiedByName = "enumToName")
    @Mapping(target = "actualRank", expression = "java(match.getActualRank() != null ? match.getActualRank().intValue() : null)")
    @Mapping(target = "radiantKills", expression = "java(match.getRadiantKills() != null && !match.getRadiantKills().isEmpty() ? match.getRadiantKills().stream().mapToInt(Integer::intValue).sum() : null)")
    @Mapping(target = "direKills", expression = "java(match.getDireKills() != null && !match.getDireKills().isEmpty() ? match.getDireKills().stream().mapToInt(Integer::intValue).sum() : null)")
    @Mapping(target = "analysisOutcome", source = "match.analysisOutcome", qualifiedByName = "enumToName")
    @Mapping(target = "bottomLaneOutcome", source = "match.bottomLaneOutcome", qualifiedByName = "enumToName")
    @Mapping(target = "midLaneOutcome", source = "match.midLaneOutcome", qualifiedByName = "enumToName")
    @Mapping(target = "topLaneOutcome", source = "match.topLaneOutcome", qualifiedByName = "enumToName")
    @Mapping(target = "playerStats", source = "playerData")
    StratzMatchResponse toStratzMatchResponse(MatchType match, MatchPlayerType playerData);

    // ==================== Player stats mapping ====================

    @Mapping(target = "heroId", expression = "java(playerData.getHero().getId() != null ? playerData.getHero().getId().intValue() : null)")
    @Mapping(target = "heroName", source = "playerData.hero.displayName")
    @Mapping(target = "kills", expression = "java(playerData.getKills() != null ? playerData.getKills().intValue() : 0)")
    @Mapping(target = "deaths", expression = "java(playerData.getDeaths() != null ? playerData.getDeaths().intValue() : 0)")
    @Mapping(target = "assists", expression = "java(playerData.getAssists() != null ? playerData.getAssists().intValue() : 0)")
    @Mapping(target = "numLastHits", expression = "java(playerData.getNumLastHits() != null ? playerData.getNumLastHits().intValue() : null)")
    @Mapping(target = "numDenies", expression = "java(playerData.getNumDenies() != null ? playerData.getNumDenies().intValue() : null)")
    @Mapping(target = "goldPerMinute", expression = "java(playerData.getGoldPerMinute() != null ? playerData.getGoldPerMinute().intValue() : null)")
    @Mapping(target = "experiencePerMinute", expression = "java(playerData.getExperiencePerMinute() != null ? playerData.getExperiencePerMinute().intValue() : null)")
    @Mapping(target = "lane", expression = "java(playerData.getLane() != null ? playerData.getLane().name() : null)")
    @Mapping(target = "position", expression = "java(playerData.getPosition() != null ? playerData.getPosition().name() : null)")
    @Mapping(target = "imp", expression = "java(playerData.getImp() != null ? playerData.getImp().intValue() : null)")
    @Mapping(target = "award", expression = "java(playerData.getAward() != null ? playerData.getAward().name() : null)")
    @Mapping(target = "campStack", expression = "java(playerData.getStats() != null && playerData.getStats().getCampStack() != null ? playerData.getStats().getCampStack().stream().mapToInt(i -> i != null ? i : 0).max().orElse(0) : 0)")
    @Mapping(target = "courierKills", expression = "java(playerData.getStats() != null && playerData.getStats().getCourierKills() != null ? playerData.getStats().getCourierKills().size() : 0)")
    @Mapping(target = "sentryWardsPurchased", expression = "java(playerData.getStats() != null && playerData.getStats().getWards() != null ? (int) playerData.getStats().getWards().stream().filter(w -> w.getType() == 0).count() : 0)")
    @Mapping(target = "observerWardsPurchased", expression = "java(playerData.getStats() != null && playerData.getStats().getWards() != null ? (int) playerData.getStats().getWards().stream().filter(w -> w.getType() == 1).count() : 0)")
    @Mapping(target = "sentryWardsDestroyed", expression = "java(playerData.getStats() != null && playerData.getStats().getWardDestruction() != null ? (int) playerData.getStats().getWardDestruction().stream().filter(w -> Boolean.FALSE.equals(w.getIsWard())).count() : 0)")
    @Mapping(target = "observerWardsDestroyed", expression = "java(playerData.getStats() != null && playerData.getStats().getWardDestruction() != null ? (int) playerData.getStats().getWardDestruction().stream().filter(w -> Boolean.TRUE.equals(w.getIsWard())).count() : 0)")
    StratzPlayerMatchStatsResponse toStratzMatchPlayerResponse(MatchPlayerType playerData);

    // ==================== Helpers ====================

    @Named("enumToName")
    default String enumToName(Enum<?> value) {
        return value != null ? value.name() : null;
    }
}

