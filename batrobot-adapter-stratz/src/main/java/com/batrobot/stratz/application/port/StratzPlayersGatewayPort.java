package com.batrobot.stratz.application.port;

import com.batrobot.stratz.application.dto.response.StratzPlayerResponse;

import java.util.List;

/**
 * Application Port for accessing Stratz Players data gateway.
 */
public interface StratzPlayersGatewayPort {

    /**
     * Fetches player data from Stratz API.
     *
     * @param steamIds64 List of 64-bit Steam IDs to fetch
     * @return List of StratzPlayerResponse in the same order as input
     */
    List<StratzPlayerResponse> fetchPlayers(List<Long> steamIds64);
}

