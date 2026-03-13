package com.batrobot.orchestration.application.exception;

import com.batrobot.orchestration.application.exception.base.OrchestrationCommandException;

/**
 * Orchestration-level exception for Stratz API unavailability.
 */
public class OrchestrationStratzUnavailableException extends OrchestrationCommandException {

    private static final String MESSAGE_KEY = "common.exception.stratz.unavailable";

    public OrchestrationStratzUnavailableException(Throwable cause) {
        super("Stratz API unavailable", cause, MESSAGE_KEY);
    }
}
