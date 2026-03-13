package com.batrobot.orchestration.application.exception.base;

import com.batrobot.shared.application.exception.ApplicationException;

import lombok.Getter;

/**
 * Base exception for orchestration command failures with i18n metadata.
 */
@Getter
public abstract class OrchestrationCommandException extends ApplicationException {

    private final String messageKey;
    private final Object[] messageArgs;

    protected OrchestrationCommandException(String technicalMessage, String messageKey, Object... messageArgs) {
        super(technicalMessage);
        this.messageKey = messageKey;
        this.messageArgs = messageArgs != null ? messageArgs.clone() : new Object[0];
    }

    protected OrchestrationCommandException(String technicalMessage, Throwable cause, String messageKey,
            Object... messageArgs) {
        super(technicalMessage, cause);
        this.messageKey = messageKey;
        this.messageArgs = messageArgs != null ? messageArgs.clone() : new Object[0];
    }
}
