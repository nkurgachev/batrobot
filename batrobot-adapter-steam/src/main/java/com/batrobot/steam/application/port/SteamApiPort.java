package com.batrobot.steam.application.port;

import com.batrobot.steam.application.dto.response.SteamPlayerResponse;
import com.batrobot.steam.application.exception.SteamUnavailableException;

import java.util.List;
import java.util.Map;

/**
 * Port interface for Steam Web API integration.
 */
public interface SteamApiPort {
       
    /**
     * Get player summaries for multiple Steam IDs in a single request.
     * Rate limiting is handled by the adapter.
     * 
     * @param steamIds List of Steam IDs (max 100 per request)
     * @return Map of steamId -> player data
     * @throws SteamUnavailableException if Steam API communication fails
     */
    Map<Long, SteamPlayerResponse> getPlayerSummariesBulk(List<Long> steamIds);
}

