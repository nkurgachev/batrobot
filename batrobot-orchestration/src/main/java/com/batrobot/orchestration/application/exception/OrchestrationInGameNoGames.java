package com.batrobot.orchestration.application.exception;

import com.batrobot.orchestration.application.exception.base.OrchestrationCommandException;

import lombok.Getter;

/**
 * Orchestration exception for /ingame when no games are found.
 */
@Getter
public class OrchestrationInGameNoGames extends OrchestrationCommandException {

    private static final String MESSAGE_KEY = "ingame.exception.no_games";
    private final String chatTitle;

    public OrchestrationInGameNoGames(Long chatId, String chatTitle) {
        super("No games found for chat " + chatId,
        MESSAGE_KEY, chatTitle);
        this.chatTitle = chatTitle;
    }
}