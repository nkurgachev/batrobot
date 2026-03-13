package com.batrobot.playerstats.infrastructure.persistence.mapper;

import com.batrobot.playerstats.domain.model.PlayerCombat;
import com.batrobot.playerstats.domain.model.PlayerEconomy;
import com.batrobot.playerstats.domain.model.PlayerKda;
import com.batrobot.playerstats.domain.model.PlayerMatchStats;
import com.batrobot.playerstats.infrastructure.persistence.entity.PlayerMatchStatsEntity;
import com.batrobot.shared.domain.model.valueobject.MatchId;
import com.batrobot.shared.domain.model.valueobject.SteamId;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * Mapper between JPA entity (PlayerMatchStats) and Domain entity (PlayerMatchStats).
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PlayerMatchStatsEntityMapper {
    
    /**
     * Maps from JPA entity to domain entity.
     * 
     * @param entity JPA entity
     * @return Domain entity
     */
    default PlayerMatchStats toDomain(PlayerMatchStatsEntity entity) {
        if (entity == null) {
            return null;
        }
        
        PlayerKda kda = new PlayerKda(entity.getKills(), entity.getDeaths(), entity.getAssists());
        PlayerEconomy economy = new PlayerEconomy(
                entity.getNumLastHits(),
                entity.getNumDenies(),
                entity.getGoldPerMinute(),
                entity.getExperiencePerMinute()
        );
        PlayerCombat combat = new PlayerCombat(
                entity.getHeroDamage(),
                entity.getTowerDamage(),
                entity.getHeroHealing()
        );
        
        return PlayerMatchStats.reconstitute(
            entity.getId(),
                MatchId.of(entity.getMatchId()),
                SteamId.fromSteamId64(entity.getSteamId()),
                entity.getHeroId(),
                entity.getHeroName(),
                entity.getIsVictory(),
                entity.getIsRadiant(),
                kda,
                economy,
                combat,
                entity.getLane(),
                entity.getPosition(),
                entity.getImp(),
                entity.getAward(),
                entity.getCampStack(),
                entity.getCourierKills(),
                entity.getSentryWardsPurchased(),
                entity.getObserverWardsPurchased(),
                entity.getSentryWardsDestroyed(),
                entity.getObserverWardsDestroyed(),
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
    @Mapping(target = "steamId", source = "steamId.value")
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
    PlayerMatchStatsEntity toEntity(PlayerMatchStats domain);
}

