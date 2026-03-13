package com.batrobot.shared.application.exception;

/**
 * Base exception for all application layer errors.
 */
public abstract class ApplicationException extends RuntimeException {
    
    protected ApplicationException(String message) {
        super(message);
    }
    
    protected ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
