package com.batrobot.steam.infrastructure.adapter;

import com.batrobot.steam.application.dto.response.SteamPlayerResponse;
import com.batrobot.steam.application.exception.SteamUnavailableException;
import com.batrobot.steam.application.mapper.SteamMapper;
import com.batrobot.steam.application.port.SteamApiPort;
import com.batrobot.steam.infrastructure.config.SteamRateLimiterNames;

import com.lukaspradel.steamapi.core.exception.SteamApiException;
import com.lukaspradel.steamapi.data.json.playersummaries.GetPlayerSummaries;
import com.lukaspradel.steamapi.data.json.playersummaries.Player;
import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;
import com.lukaspradel.steamapi.webapi.request.SteamWebApiRequest;
import com.lukaspradel.steamapi.webapi.request.builders.SteamWebApiRequestFactory;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter implementation of SteamApiPort.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SteamApiAdapter implements SteamApiPort {

    private final SteamWebApiClient steamWebApiClient;
    private final SteamMapper steamMapper;
    
    @Override
    @RateLimiter(name = SteamRateLimiterNames.STEAM_API)
    public Map<Long, SteamPlayerResponse> getPlayerSummariesBulk(List<Long> steamIds) {
        if (steamIds == null || steamIds.isEmpty()) {
            return new HashMap<>();
        }
        
        log.debug("Fetching player summaries for {} Steam IDs", steamIds.size());
        
        try {
            List<String> steamIdStrings = steamIds.stream()
                .map(String::valueOf)
                .toList();
            SteamWebApiRequest request = SteamWebApiRequestFactory.createGetPlayerSummariesRequest(steamIdStrings);
            GetPlayerSummaries response = steamWebApiClient.processRequest(request);
            
            List<Player> players = response.getResponse().getPlayers();
            
            if (players == null || players.isEmpty()) {
                log.debug("No players found for provided Steam IDs");
                return new HashMap<>();
            }
            
            Map<Long, SteamPlayerResponse> result = new HashMap<>();
            
            for (Player player : players) {
                Long steamId = Long.valueOf(player.getSteamid());
                SteamPlayerResponse steamPlayerDto = steamMapper.toResponse(player);
                
                result.put(steamId, steamPlayerDto);
            }
            
            log.debug("Successfully fetched {} player summaries", result.size());
            return result;
        } catch (SteamApiException e) {
            log.error("Failed to fetch player summaries from Steam API", e);
            throw new SteamUnavailableException("Failed to fetch player summaries", e);
        }
    }
}



