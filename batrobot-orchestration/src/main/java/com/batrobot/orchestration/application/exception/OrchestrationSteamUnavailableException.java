package com.batrobot.orchestration.application.exception;

import com.batrobot.orchestration.application.exception.base.OrchestrationCommandException;

/**
 * Orchestration exception for /ingame when Steam service is unavailable.
 */
public class OrchestrationSteamUnavailableException extends OrchestrationCommandException {

    private static final String MESSAGE_KEY = "common.exception.steam.unavailable";

    public OrchestrationSteamUnavailableException(Throwable cause) {
        super("Steam API unavailable", cause, MESSAGE_KEY);
    }
}
