package com.batrobot.bot.infrastructure.telegram.formatter.command;

import com.batrobot.bot.infrastructure.telegram.formatter.base.BaseResultFormatter;
import com.batrobot.orchestration.application.dto.response.SetEmojiCommandResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Formatter for set_emoji command results.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SetEmojiResultFormatter extends BaseResultFormatter {

    private static final String MESSAGE_KEY_UPDATED = "set_emoji.success.updated";

    private final MessageSource messageSource;

    public String formatResult(SetEmojiCommandResponse response, String languageCode) {
        Locale locale = resolveLocale(languageCode);

        String userLabel = response.telegramUsername() != null
                ? mention(response.telegramUsername())
                : String.valueOf(response.telegramUserId());

        return messageSource.getMessage(
                MESSAGE_KEY_UPDATED,
                new Object[] { userLabel, response.emoji() },
                locale);
    }
}
