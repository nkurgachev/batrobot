package com.batrobot.orchestration.application.exception;

import com.batrobot.orchestration.application.exception.base.OrchestrationCommandException;

/**
 * Orchestration exception for /reps when there are no participants.
 */
public class OrchestrationRepsNoParticipantsException extends OrchestrationCommandException {

    private static final String MESSAGE_KEY = "reps.exception.no_participants";

    public OrchestrationRepsNoParticipantsException(Long chatId) {
        super("No participants resolved for chat " + chatId, 
        MESSAGE_KEY);
    }
}
