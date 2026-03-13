package com.batrobot.player.domain.specification;

import com.batrobot.player.domain.exception.PlayerAlreadyExistsException;
import com.batrobot.player.domain.repository.PlayerRepository;
import com.batrobot.shared.domain.model.valueobject.SteamId;
import com.batrobot.shared.domain.specification.Specification;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * Specification for ensuring that a player (identified by Steam ID) is not already existing.
 */
@RequiredArgsConstructor
public class UniquePlayerSpecification implements
        Specification<UniquePlayerSpecification.PlayerContext> {

    private final PlayerRepository playerRepository;

    /**
     * Context for checking the specification.
     * Contains all necessary data for checking uniqueness.
     */
    @Value
    @Builder
    public static class PlayerContext {
        SteamId steamId;
    }

    @Override
    public boolean isSatisfiedBy(PlayerContext context) {
        return playerRepository
                .existsBySteamId(context.getSteamId());
    }

    @Override
    public void check(PlayerContext context) {
        if (!isSatisfiedBy(context)) {
            throw new PlayerAlreadyExistsException(context.getSteamId());
        }
    }
}
