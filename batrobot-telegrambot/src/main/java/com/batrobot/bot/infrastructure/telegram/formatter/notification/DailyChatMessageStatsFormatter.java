package com.batrobot.bot.infrastructure.telegram.formatter.notification;

import java.util.List;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.batrobot.bot.infrastructure.telegram.formatter.base.BaseResultFormatter;
import com.batrobot.bot.infrastructure.telegram.formatter.base.TelegramTemplateRenderer;

import lombok.RequiredArgsConstructor;

/**
 * Formats daily non-command message count notifications for Telegram chats.
 */
@Component
@RequiredArgsConstructor
public class DailyChatMessageStatsFormatter extends BaseResultFormatter {

    private static final String TEMPLATE_NAME = "ntf-daily-message-stats";
    private static final String MESSAGE_KEY_HEADER = "notification.daily_message_stats.template.header";

    private final MessageSource messageSource;
    private final TelegramTemplateRenderer templateRenderer;

    public String format(Map<String, Integer> countsByUsername, String languageCode) {
        List<Map<String, Object>> users = countsByUsername.entrySet().stream()
                .map(entry -> Map.<String, Object>of(
                        "telegramUsername", entry.getKey(),
                        "count", entry.getValue()))
                .toList();

        return templateRenderer.render(TEMPLATE_NAME, Map.of(
                "header", messageSource.getMessage(MESSAGE_KEY_HEADER, null, resolveLocale(languageCode)),
                "users", users));
    }
}