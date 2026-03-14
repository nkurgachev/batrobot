package com.batrobot.bot.infrastructure.telegram.formatter.notification;

import lombok.RequiredArgsConstructor;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.batrobot.bot.infrastructure.config.LocaleOverrideProperties;
import com.batrobot.bot.infrastructure.telegram.formatter.base.BaseResultFormatter;
import com.batrobot.bot.infrastructure.telegram.formatter.base.TelegramTemplateRenderer;
import com.batrobot.orchestration.application.dto.response.MatchResultNotificationDataResponse.MatchNotificationTarget;
import com.batrobot.shared.application.port.config.AppDayTimeConfig;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Formats match result notification messages for Telegram.
 */
@Component
@RequiredArgsConstructor
public class MatchResultNotificationFormatter extends BaseResultFormatter {

    private static final String TEMPLATE_NAME = "ntf-match-result";
    private static final String STRATZ_MATCH_URL = "https://stratz.com/matches/";
    private static final String MESSAGE_KEY_HEADER = "notification.match.template.header";
    private static final String MESSAGE_KEY_ON = "notification.match.template.on";

    private final LocaleOverrideProperties localeProperties;
    private final AppDayTimeConfig dayTimeConfig;
    private final MessageSource messageSource;
    private final TelegramTemplateRenderer templateRenderer;

    public String formatResult(MatchNotificationTarget target,
            Long matchId,
            Long startDateTime,
            String heroName,
            Boolean isVictory,
            Integer kills,
            Integer deaths,
            Integer assists,
            String position,
            String award,
            Integer imp) {
        Locale locale = resolveNotificationLocale(localeProperties);

        Map<String, Object> model = new HashMap<>();
        model.put("header", messageSource.getMessage(MESSAGE_KEY_HEADER, null, locale));
        model.put("fullName", formatFullName(target.firstName(), target.lastName(),
                mention(target.telegramUsername())));
        model.put("on", messageSource.getMessage(MESSAGE_KEY_ON, null, locale));
        model.put("steamUsername", target.steamUsername() != null ? target.steamUsername() : "unknown");
        model.put("matchUrl", STRATZ_MATCH_URL + matchId);
        model.put("lobbyType", target.lobbyType() != null ? formatLobbyType(target.lobbyType()) : "");
        model.put("gameMode", target.gameMode() != null ? formatGameMode(target.gameMode()) : "");
        model.put("matchTime", formatDateTime(startDateTime, dayTimeConfig.getTimezone()));
        model.put("result", formatResult(isVictory));
        model.put("heroName", heroName != null ? heroName : "unknown");
        model.put("kda", formatKda(kills, deaths, assists));

        String formattedPosition = position != null ? formatPosition(position) : null;
        if (formattedPosition != null) {
            model.put("position", formattedPosition);
        }

        String formattedAward = award != null ? formatAward(award) : null;
        if (formattedAward != null) {
            model.put("award", formattedAward);
        }

        String formattedImp = imp != null ? formatImp(imp) : null;
        if (formattedImp != null && !formattedImp.isEmpty()) {
            model.put("imp", formattedImp);
        }

        return templateRenderer.render(TEMPLATE_NAME, model);
    }

    private String formatKda(Integer kills, Integer deaths, Integer assists) {
        int k = kills != null ? kills : 0;
        int d = deaths != null ? deaths : 0;
        int a = assists != null ? assists : 0;
        return k + "/" + d + "/" + a;
    }
}
