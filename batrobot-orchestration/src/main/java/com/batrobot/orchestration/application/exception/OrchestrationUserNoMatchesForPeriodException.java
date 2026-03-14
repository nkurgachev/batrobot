package com.batrobot.orchestration.application.exception;

import com.batrobot.orchestration.application.exception.base.OrchestrationCommandException;

/**
 * Orchestration exception for period stats commands when user has no matches.
 */
public class OrchestrationUserNoMatchesForPeriodException extends OrchestrationCommandException {

    private static final String MESSAGE_KEY = "stats_period.exception.no_matches";

    public OrchestrationUserNoMatchesForPeriodException(Long chatId, Long userId) {
        super("No matches found for user " + userId + " in chat " + chatId + " for selected period",
                MESSAGE_KEY);
    }
}