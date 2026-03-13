package com.batrobot.orchestration.application.exception;

import com.batrobot.orchestration.application.exception.base.OrchestrationCommandException;
import com.batrobot.shared.domain.model.valueobject.SteamId;

import lombok.Getter;

/**
 * Orchestration-level exception for duplicate player binding.
 */
@Getter
public class OrchestrationPlayerAlreadyBoundException extends OrchestrationCommandException {

    private static final String MESSAGE_KEY = "bind.exception.already_registered";
    private final String steamUsername;
    private final SteamId steamId;
    private final String telegramUsername;

    public OrchestrationPlayerAlreadyBoundException(String steamUsername, SteamId steamId, String telegramUsername) {
        super(
                "Player " + steamUsername + " (steamId: " + steamId + ") already bound to user: " + telegramUsername,
                MESSAGE_KEY, steamUsername, steamId, telegramUsername);
        this.steamId = steamId;
        this.steamUsername = steamUsername;
        this.telegramUsername = telegramUsername;
    }
}
