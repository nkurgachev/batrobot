package com.batrobot.orchestration.application.dto.response;

import java.util.List;

/**
 * BFF Response for reps command.
 */
public record RepsCommandResponse(
    List<UserReputation> users) {
    
    /**
     * Reputation information for a single user.
     */
    public record UserReputation(
        String telegramUsername,
        String firstName,
        String lastName,
        String emoji,
        Integer reputation
    ) {
    }
}

