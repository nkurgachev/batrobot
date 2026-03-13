package com.batrobot.orchestration.application.exception;

import com.batrobot.orchestration.application.exception.base.OrchestrationCommandException;

/**
 * Orchestration exception when a Telegram user is not found in the database.
 */
public class OrchestrationUserNotFoundException extends OrchestrationCommandException {

    private static final String MESSAGE_KEY = "common.exception.user_not_found";

    public OrchestrationUserNotFoundException(Long telegramUserId) {
        super("Telegram user not found: " + telegramUserId,
                MESSAGE_KEY, telegramUserId);
    }
}
