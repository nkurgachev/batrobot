package com.batrobot.orchestration.application.exception;

import com.batrobot.orchestration.application.exception.base.OrchestrationCommandException;

import lombok.Getter;

/**
 * Orchestration exception for /all_pubs_today when no matches are found.
 */
@Getter
public class OrchestrationAllPubsTodayNoMatchesException extends OrchestrationCommandException {

    private static final String MESSAGE_KEY = "all_pubs_today.exception.no_matches";
    private final String chatTitle;

    public OrchestrationAllPubsTodayNoMatchesException(Long chatId, String chatTitle) {
        super("No matches found for chat " + chatId,
        MESSAGE_KEY, chatTitle);
        this.chatTitle = chatTitle;
    }
}
