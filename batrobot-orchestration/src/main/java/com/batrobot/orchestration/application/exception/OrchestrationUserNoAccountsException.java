package com.batrobot.orchestration.application.exception;

import com.batrobot.orchestration.application.exception.base.OrchestrationCommandException;

/**
 * Orchestration exception for /me when user has no linked accounts in chat.
 */
public class OrchestrationUserNoAccountsException extends OrchestrationCommandException {

    private static final String MESSAGE_KEY = "common.exception.user_no_accounts";

    public OrchestrationUserNoAccountsException(Long chatId, Long userId) {
        super("No linked accounts for user " + userId + " in chat " + chatId, 
        MESSAGE_KEY);
    }
}
