package com.batrobot.bot.infrastructure.telegram.command.mapper;

import com.batrobot.bot.infrastructure.telegram.command.exception.TelegramCommandInputException;

/**
 * Shared helper for steamId argument parsing from telegram command arguments.
 */
public interface SteamIdArgumentMapperSupport {

    default Long parseSteamId(String[] args, String missingKey, String invalidKey) {
        if (args == null || args.length == 0 || args[0] == null || args[0].isBlank()) {
            throw new TelegramCommandInputException(missingKey);
        }

        String steamIdRaw = args[0].trim();
        try {
            return Long.parseLong(steamIdRaw);
        } catch (NumberFormatException ex) {
            throw new TelegramCommandInputException(invalidKey, steamIdRaw);
        }
    }
}
