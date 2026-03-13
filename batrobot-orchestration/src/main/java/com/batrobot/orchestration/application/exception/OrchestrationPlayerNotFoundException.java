package com.batrobot.orchestration.application.exception;

import com.batrobot.orchestration.application.exception.base.OrchestrationCommandException;
import com.batrobot.shared.domain.model.valueobject.SteamId;

import lombok.Getter;

/**
 * Orchestration-level exception for missing player by Steam ID.
 */
@Getter
public class OrchestrationPlayerNotFoundException extends OrchestrationCommandException {

    private static final String MESSAGE_KEY = "common.exception.player_not_found";
    private final SteamId steamId;

    public OrchestrationPlayerNotFoundException(SteamId steamId) {
        super("Player not found: " + steamId,
                MESSAGE_KEY, steamId);
        this.steamId = steamId;
    }
}
