package com.batrobot.stratz.application.usecase.query;

import com.batrobot.stratz.application.dto.response.StratzMatchesResponse;
import com.batrobot.stratz.application.port.StratzMatchesGatewayPort;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Query Use Case for fetching matches from Stratz API for a single player.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class GetMatchesForPlayerQuery {

    private final StratzMatchesGatewayPort stratzMatchesGateway;

    /**
     * Fetches matches for a single player from Stratz API.
     *
     * @param steamId   Player's Steam ID (64-bit)
     * @param startTime Unix timestamp for start time filter
     * @param take      Maximum number of matches to fetch
     * @return StratzMatchesResponse with fetched matches (matches list can be empty)
     */
    public StratzMatchesResponse execute(@Valid @NotNull Long steamId,
                                         @NotNull Long startTime,
                                         @NotNull Integer take) {
        log.debug("Fetching matches for player {}: startTime={}, take={}", steamId, startTime, take);

        StratzMatchesResponse response = stratzMatchesGateway.fetchMatches(steamId, startTime, take);
        if (response == null) {
            return new StratzMatchesResponse(steamId, List.of());
        }
        if (response.getMatches() == null) {
            response.setMatches(List.of());
        }
        return response;
    }
}


