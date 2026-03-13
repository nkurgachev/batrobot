package com.batrobot.orchestration.application.exception;

import com.batrobot.orchestration.application.exception.base.OrchestrationCommandException;
import com.batrobot.shared.domain.model.valueobject.SteamId;

import lombok.Getter;

/**
 * Orchestration-level exception for missing binding for Steam ID.
 */
@Getter
public class OrchestrationPlayerBindingNotFoundException extends OrchestrationCommandException {

    private static final String MESSAGE_KEY = "unbind.exception.not_registered";
    private final String steamUsername;
    private final SteamId steamId;
    private final String telegramUsername;

    public OrchestrationPlayerBindingNotFoundException(String steamUsername,SteamId steamId, String telegramUsername) {
        super("Player " + steamUsername + " (steamId: " + steamId + ") didn't bound to user: " + telegramUsername,
                MESSAGE_KEY, steamUsername, steamId, telegramUsername);
        this.steamUsername = steamUsername;
        this.steamId = steamId;
        this.telegramUsername = telegramUsername;
    }
}
