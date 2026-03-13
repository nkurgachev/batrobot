package com.batrobot.bot.infrastructure.telegram.formatter.notification;

import lombok.RequiredArgsConstructor;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.batrobot.bot.infrastructure.telegram.formatter.base.BaseResultFormatter;
import com.batrobot.bot.infrastructure.telegram.formatter.base.TelegramTemplateRenderer;
import com.batrobot.orchestration.application.dto.response.PlayerNotificationDataResponse.NotificationTarget;

import java.util.Locale;
import java.util.Map;

/**
 * Formats Steam username change notification messages for Telegram.
 */
@Component
@RequiredArgsConstructor
public class UsernameChangeNotificationFormatter extends BaseResultFormatter {

    private static final String TEMPLATE_NAME = "ntf-username-change";
    private static final String MESSAGE_KEY_HEADER = "notification.username.template.header";
    private static final String MESSAGE_KEY_CHANGED_FROM = "notification.username.template.changed_from";
    private static final String MESSAGE_KEY_CHANGED_TO = "notification.username.template.changed_to";

    private static final String MESSAGE_KEY_POLL_QUESTION = "notification.username.poll_question";

    private final MessageSource messageSource;
    private final TelegramTemplateRenderer templateRenderer;

    public String formatResult(NotificationTarget target, String oldSteamUsername, String newSteamUsername) {
        Locale locale = Locale.getDefault();

        Map<String, Object> model = Map.of(
                "header", messageSource.getMessage(MESSAGE_KEY_HEADER, null, locale),
                "changedNameFrom", messageSource.getMessage(MESSAGE_KEY_CHANGED_FROM, null, locale),
                "changedNameTo", messageSource.getMessage(MESSAGE_KEY_CHANGED_TO, null, locale),
                "userMention", mention(target.telegramUsername()),
                "oldName", bold(oldSteamUsername),
                "newName", bold(newSteamUsername));

        return templateRenderer.render(TEMPLATE_NAME, model);
    }

    public String formatPollQuestion() {
        Locale locale = Locale.getDefault();
        return messageSource.getMessage(MESSAGE_KEY_POLL_QUESTION, null, locale);
    }
}
