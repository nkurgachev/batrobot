package com.batrobot.stratz.application.port;

import com.batrobot.stratz.application.dto.response.StratzMatchesResponse;

/**
 * Application Port for accessing Stratz Matches data gateway.
 */
public interface StratzMatchesGatewayPort {

     /**
     * Fetches matches for a single player from Stratz API.
     *
     * @param steamId64 Player's 64-bit Steam ID
     * @param startTime Unix timestamp (seconds) — fetch matches after this time
     * @param take Maximum number of matches to fetch
     * @return StratzMatchesResponse with match list (can be empty)
     */
    StratzMatchesResponse fetchMatches(Long steamId64, long startTime, int take);
}

