package com.batrobot.orchestration.application.dto.response;

import java.util.List;

/**
 * BFF Response for users in game command.
 */
public record InGameCommandResponse(
        List<UserGameStatus> usersInGame) {
    
    /**
     * Game status for a single Telegram user.
     */
    public record UserGameStatus(
            Long telegramUserId,
            String telegramUsername,
            String firstName,
            String lastName,
            List<GameInfo> games) {
        
        /**
         * Information about a single game being played on a Steam account.
         */
        public record GameInfo(
                Long steamId64,
                String steamUsername,
                String gameName,
                Long gameId) {
        }
    }
}
