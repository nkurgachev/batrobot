package com.batrobot.orchestration.application.dto.response;

import java.util.List;

/**
 * BFF Response for top pubers command.
 * Players are grouped by seasonal rank, sorted by rank descending (null ranks last).
 * Within each group, players are sorted alphabetically by Telegram username.
 */
public record TopPubersResponse(
        List<RankGroup> rankGroups) {

    /**
     * A group of players sharing the same seasonal rank.
     */
    public record RankGroup(
            Integer seasonRank,
            List<PuberInfo> players) {
    }

    /**
     * Information about a single player (puber) in the chat.
     */
    public record PuberInfo(
            Long telegramUserId,
            String firstName,
            String lastName,
            String telegramUsername,
            String emoji,
            String steamUsername,
            Integer seasonRank) {
    }
}