package com.batrobot.orchestration.application.exception;

import com.batrobot.orchestration.application.exception.base.OrchestrationCommandException;

import lombok.Getter;

/**
 * Orchestration exception for when chat has no linked accounts.
 */
@Getter
public class OrchestrationNoAccountsException extends OrchestrationCommandException {

    private static final String MESSAGE_KEY = "common.exception.no_accounts";
    private final String chatTitle;

    public OrchestrationNoAccountsException(Long chatId, String chatTitle) {
        super("No linked accounts for chat " + chatId,
        MESSAGE_KEY, chatTitle);
        this.chatTitle = chatTitle;
    }
}
