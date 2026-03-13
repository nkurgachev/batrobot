package com.batrobot.steam.application.dto.response;

/**
 * Intermediate DTO representing player data from Steam API.
 */
public record SteamPlayerResponse (
    Long steamId64,
    String steamUsername,
    
    String gameName,
    Long gameId
){}

