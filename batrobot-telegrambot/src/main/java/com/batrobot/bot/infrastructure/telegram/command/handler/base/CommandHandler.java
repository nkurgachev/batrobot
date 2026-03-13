package com.batrobot.bot.infrastructure.telegram.command.handler.base;

import java.util.Map;

import com.batrobot.bot.infrastructure.telegram.command.dto.CommandEnvelope;

/**
 * Interface for handling Telegram bot commands
 */
public interface CommandHandler {
    String handle(CommandEnvelope envelope);

    /**
     * Resolves effective language code, applying per-user overrides if configured.
     *
     * @param envelope command envelope with user and metadata
     * @param overrides map of telegram username to language code
     * @return resolved language code
     */
    default String resolveLanguageCode(CommandEnvelope envelope, Map<String, String> overrides) {
        String username = envelope.payload().user().username();
        if (username != null && overrides != null && overrides.containsKey(username)) {
            return overrides.get(username);
        }
        return envelope.metadata().languageCode();
    }
}
