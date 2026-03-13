package com.batrobot.steam.application.exception;

import com.batrobot.shared.application.exception.ApplicationException;

/**
 * Exception thrown when Steam API (external service) is unavailable or returns an error.
 */
public class SteamUnavailableException extends ApplicationException {
    public SteamUnavailableException(String message) {
        super(message);
    }

    public SteamUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}

