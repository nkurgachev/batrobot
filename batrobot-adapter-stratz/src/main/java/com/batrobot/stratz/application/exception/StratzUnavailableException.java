package com.batrobot.stratz.application.exception;

import com.batrobot.shared.application.exception.ApplicationException;

/**
 * Exception thrown when Stratz API (external service) is unavailable or returns an error.
 */
public class StratzUnavailableException extends ApplicationException {
    
    public StratzUnavailableException(String message) {
        super(message);
    }
    
    public StratzUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}

