package com.batrobot.player.domain.exception;

import com.batrobot.shared.domain.exception.DomainException;
import com.batrobot.shared.domain.model.valueobject.SteamId;

import lombok.Getter;

/**
 * Thrown when attempting to create a player that already exists.
 */
@Getter
public class PlayerAlreadyExistsException extends DomainException {

    private final SteamId steamId;

    public PlayerAlreadyExistsException(SteamId steamId) {
        super("Player with Steam ID " + steamId + " already exists");
        this.steamId = steamId;
    }
}

