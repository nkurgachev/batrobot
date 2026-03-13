package com.batrobot.player.application.exception;

import com.batrobot.shared.application.exception.ApplicationException;
import com.batrobot.shared.domain.model.valueobject.SteamId;

import lombok.Getter;

/**
 * Thrown when a provided Steam ID is invalid or not found.
 */
@Getter
public class PlayerNotFoundException extends ApplicationException {

    private final SteamId steamId;

    public PlayerNotFoundException(SteamId steamId) {
        super("Player not found: " + steamId);
        this.steamId = steamId;
    }
}

