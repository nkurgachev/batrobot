package com.batrobot.orchestration.application.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * BFF Response for me command.
 */
public record MeCommandResponse(
        List<PlayerRankHistory> players) {

    /**
     * Rank history for a single Steam account.
     */
    public record PlayerRankHistory(
            Long steamId64,
            String steamUsername,
            List<RankInfo> rankHistory) {

        /**
         * DTO representing rank information for a specific season.
         */
        public record RankInfo(
                Integer seasonRank,
                OffsetDateTime assignedAt) {
        }
    }
}
