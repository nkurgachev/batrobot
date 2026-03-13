package com.batrobot.bot.infrastructure.telegram.formatter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.batrobot.bot.infrastructure.telegram.formatter.base.BaseResultFormatter;
import com.batrobot.orchestration.application.dto.response.BindCommandResponse;

import java.util.Locale;

/**
 * Formatter for bind command results.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BindResultFormatter extends BaseResultFormatter {

    private final static String MESSAGE_KEY_BINDING_CREATED = "bind.success.binding_created";

    private final MessageSource messageSource;

    /**
     * Formats binding result into a user-friendly message with HTML formatting.
     *
     * @param binding      the created Steam binding
     * @param languageCode telegram language code
     * @return formatted HTML message string
     */
    public String formatResult(BindCommandResponse binding, String languageCode) {
        log.debug("Formatting bind success for Steam account {}", binding.steamId64());

        Locale locale = resolveLocale(languageCode);

        String steamAccountName = binding.steamUsername() != null ? bold(binding.steamUsername()) : bold("Unknown");

        String steamId = monospace(String.valueOf(binding.steamId64()));

        String telegramUsername = binding.telegramUsername() != null ? italic(binding.telegramUsername()) : italic("Unknown");

        String message = messageSource.getMessage(
                MESSAGE_KEY_BINDING_CREATED,
                new Object[] {
                    steamAccountName,
                    steamId,
                    telegramUsername
                },
                locale);

        log.debug("Successfully formatted bind success message");
        return message;
    }

}
