package com.batrobot.shared.domain.model.valueobject;

import com.batrobot.shared.domain.exception.valueobject.InvalidSteamIdException;

/**
 * Value Object representing Steam ID (64-bit identifier).
 */
public record SteamId(Long value) {
    
    private static final long MIN_STEAM_ID = 76561197960265728L; // Minimum valid Steam ID 64
    
    public SteamId {
        if (value == null) {
            throw new InvalidSteamIdException("Steam ID cannot be null");
        }
        if (value < MIN_STEAM_ID) {
            throw new InvalidSteamIdException(
                String.format("Invalid Steam ID: %d. Must be >= %d", value, MIN_STEAM_ID)
            );
        }
    }
    
    /**
     * Creates SteamId from 64-bit Steam ID with validation.
     * @param steamId64 64-bit Steam ID
     * @return SteamId value object
     * @throws InvalidSteamIdException if ID is invalid
     */
    public static SteamId fromSteamId64(Long steamId64) {
        return new SteamId(steamId64);
    }
       
    /**
     * Creates SteamId from 32-bit Steam ID (account ID).
     * Converts 32-bit format to 64-bit by adding the base offset.
     * @param steamId32 32-bit Steam ID (account ID)
     * @return SteamId value object
     * @throws InvalidSteamIdException if ID is invalid
     */
    public static SteamId fromSteamId32(Long steamId32) {
        return new SteamId(steamId32 + MIN_STEAM_ID);
    }

    /**
     * Returns the 32-bit Steam ID (account ID part).
     */
    public Long toSteamId32() {
        return value - MIN_STEAM_ID;
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}
