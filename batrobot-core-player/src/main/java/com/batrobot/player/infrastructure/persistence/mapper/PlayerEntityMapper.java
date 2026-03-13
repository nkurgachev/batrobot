package com.batrobot.player.infrastructure.persistence.mapper;

import com.batrobot.player.domain.model.Player;
import com.batrobot.player.infrastructure.persistence.entity.PlayerEntity;
import com.batrobot.shared.domain.model.valueobject.SeasonRank;
import com.batrobot.shared.domain.model.valueobject.SteamId;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * Mapper between JPA Entity and Domain Model for Player.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PlayerEntityMapper {

    /**
     * Converts JPA entity to Domain entity (reconstitution).
     * 
     * @param entity JPA entity
     * @return Domain entity
     */
    default Player toDomain(PlayerEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Player.reconstitute(
            entity.getId(),
            SteamId.fromSteamId64(entity.getSteamId()),
            entity.getName(),
            entity.getProfileUrl(),
            entity.getAvatarUrl(),
            entity.getAccountCreationDate(),
            entity.getCommunityVisibleState(),
            entity.getIsAnonymous(),
            entity.getIsStratzPublic(),
            entity.getIsDotaPlusSubscriber(),
            entity.getSmurfFlag(),
            SeasonRank.of(entity.getSeasonRank()),
            entity.getActivity(),
            entity.getImp(),
            entity.getMatchCount(),
            entity.getWinCount(),
            entity.getFirstMatchDate(),
            entity.getLastMatchDate(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
    
    /**
     * Converts Domain entity to JPA entity for persistence.
     * 
     * @param domain Domain entity
     * @return JPA entity
     */
    @Mapping(target = "steamId", source = "steamId.value")
    @Mapping(target = "name", source = "steamUsername")
    @Mapping(target = "seasonRank", source = "seasonRank.value")
    PlayerEntity toEntity(Player domain);
}

