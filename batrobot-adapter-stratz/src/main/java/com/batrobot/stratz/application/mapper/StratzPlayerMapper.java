package com.batrobot.stratz.application.mapper;

import com.batrobot.stratz.application.dto.response.StratzPlayerResponse;
import com.batrobot.shared.domain.model.valueobject.SteamId;
import com.batrobot.stratz.generated.types.PlayerType;

import org.mapstruct.*;

/**
 * Infrastructure mapper: Stratz GraphQL PlayerType → StratzPlayerResponse (intermediate DTO).
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StratzPlayerMapper {

    // ==================== Stratz GraphQL PlayerType → StratzPlayerResponse ====================

    /**
     * Maps Stratz GraphQL PlayerType to StratzPlayerResponse.
     * This mapping handles the conversion of nested structures and type transformations.
     *
     * @param player The player data from Stratz GraphQL API
     * @return Fully populated StratzPlayerResponse with 64-bit Steam ID
     */
    @Mapping(target = "steamId64", source = "steamAccount.id", qualifiedByName = "steamId32to64")
    @Mapping(target = "avatarUrl", source = "steamAccount.avatar")
    @Mapping(target = "steamUsername", source = "steamAccount.name")
    @Mapping(target = "profileUrl", source = "steamAccount.profileUri")
    @Mapping(target = "timeCreated", source = "steamAccount.timeCreated")
    @Mapping(target = "communityVisibleState", source = "steamAccount.communityVisibleState", qualifiedByName = "numberToInt")
    @Mapping(target = "isAnonymous", source = "steamAccount.isAnonymous")
    @Mapping(target = "isDotaPlusSubscriber", source = "steamAccount.isDotaPlusSubscriber")
    @Mapping(target = "isStratzPublic", source = "steamAccount.isStratzPublic")
    @Mapping(target = "smurfFlag", source = "steamAccount.smurfFlag", qualifiedByName = "numberToInt")
    @Mapping(target = "seasonRank", source = "steamAccount.seasonRank", qualifiedByName = "numberToInt")
    @Mapping(target = "activity", source = "steamAccount.activity", qualifiedByName = "extractActivity")
    StratzPlayerResponse toStratzPlayerResponse(PlayerType player);

    // ==================== Helper Methods ====================

    /**
     * Converts 32-bit Steam ID (from Stratz API) to 64-bit Steam ID (for database).
     * Uses fromSteamId32() constructor for explicit and consistent conversion.
     */
    @Named("steamId32to64")
    default Long steamId32to64(Long steamId32) {
        return SteamId.fromSteamId32(steamId32).value();
    }

    /**
     * Converts Number to Integer, handling null values.
     */
    @Named("numberToInt")
    default Integer numberToInt(Number value) {
        return value != null ? value.intValue() : null;
    }

    /**
     * Extracts activity value from nested SteamAccountActivityType.
     * The GraphQL structure is: steamAccount.activity.activity
     */
    @Named("extractActivity")
    default String extractActivity(Object activityObj) {
        if (activityObj == null) {
            return null;
        }
        try {
            var method = activityObj.getClass().getMethod("getActivity");
            Object result = method.invoke(activityObj);
            return result != null ? result.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }
}

