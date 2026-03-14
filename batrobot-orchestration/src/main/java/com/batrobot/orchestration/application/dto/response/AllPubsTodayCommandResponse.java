package com.batrobot.orchestration.application.dto.response;

import java.util.List;

/**
 * BFF Response for grouping matches by Telegram user.
 */
public record AllPubsTodayCommandResponse(
        List<UserMatchHistory> userMatches) {

    public record UserMatchHistory(
            Long telegramUserId,
            String telegramUsername,
            String firstName,
            String lastName,
            List<PlayerMatchHistory> players) {

        /**
         * DTO representing match information for a player.
         */
        public record PlayerMatchHistory(
                Long steamId64,
                String steamUsername,

                List<MatchStats> matches) {

            /**
             * DTO representing player match statistics for a player.
             */
            public record MatchStats(
                    Long matchId,
                    Long startDateTime,
                    Boolean isVictory,
                    String lobbyType,
                    String gameMode,

                    String heroName,
                    String position,
                    Integer kills,
                    Integer deaths,
                    Integer assists,
                    String award,
                    Integer imp) {
            }
        }
    }
}