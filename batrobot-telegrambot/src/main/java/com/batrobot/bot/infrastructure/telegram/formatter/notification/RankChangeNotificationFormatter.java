package com.batrobot.bot.infrastructure.telegram.formatter.notification;

import lombok.RequiredArgsConstructor;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.batrobot.bot.infrastructure.config.LocaleOverrideProperties;
import com.batrobot.bot.infrastructure.telegram.formatter.base.BaseResultFormatter;
import com.batrobot.bot.infrastructure.telegram.formatter.base.TelegramTemplateRenderer;
import com.batrobot.orchestration.application.dto.response.PlayerNotificationDataResponse.NotificationTarget;
import com.batrobot.shared.domain.model.valueobject.SeasonRank;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Formats rank change notification messages for Telegram.
 */
@Component
@RequiredArgsConstructor
public class RankChangeNotificationFormatter extends BaseResultFormatter {

    private static final String TEMPLATE_NAME = "ntf-rank-change";
    private static final String MESSAGE_KEY_HEADER = "notification.rank.template.header";
    private static final String MESSAGE_KEY_UP_FROM = "notification.rank.template.up_from";
    private static final String MESSAGE_KEY_UP_TO = "notification.rank.template.up_to";
    private static final String MESSAGE_KEY_DOWN_TO = "notification.rank.template.down_to";

    private final LocaleOverrideProperties localeProperties;
    private final MessageSource messageSource;
    private final TelegramTemplateRenderer templateRenderer;

    /**
     * Formats a rank change notification message.
     *
     * @param target notification target (user info)
     * @param oldRank previous season rank
     * @param newRank new season rank
     * @return formatted HTML message
     */
    public String formatResult(NotificationTarget target, SeasonRank oldRank, SeasonRank newRank) {
        Locale locale = resolveNotificationLocale(localeProperties);

        Map<String, Object> model = new HashMap<>();
        model.put("header", messageSource.getMessage(MESSAGE_KEY_HEADER, null, locale));
        model.put("isRankUp", isRankUp(oldRank, newRank));
        model.put("rankedUpFrom", messageSource.getMessage(MESSAGE_KEY_UP_FROM, null, locale));
        model.put("rankedUpTo", messageSource.getMessage(MESSAGE_KEY_UP_TO, null, locale));
        model.put("rankedDownTo", messageSource.getMessage(MESSAGE_KEY_DOWN_TO, null, locale));
        model.put("userMention", mention(target.telegramUsername()));
        model.put("oldRank", bold(formatRankAsMedal(
                oldRank != null ? oldRank.value() : null, messageSource, locale)));
        model.put("newRank", bold(formatRankAsMedal(
                newRank != null ? newRank.value() : null, messageSource, locale)));

        return templateRenderer.render(TEMPLATE_NAME, model);
    }

    private boolean isRankUp(SeasonRank oldRank, SeasonRank newRank) {
        int oldValue = oldRank != null && oldRank.value() != null ? oldRank.value() : 0;
        int newValue = newRank != null && newRank.value() != null ? newRank.value() : 0;
        return newValue > oldValue;
    }
}
