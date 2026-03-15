package com.batrobot.bot.infrastructure.telegram.formatter.command;

import com.batrobot.bot.infrastructure.telegram.formatter.base.BaseResultFormatter;
import com.batrobot.bot.infrastructure.telegram.formatter.base.TelegramTemplateRenderer;
import com.batrobot.orchestration.application.dto.response.StatsPeriodCommandResponse;
import com.batrobot.orchestration.application.dto.response.StatsPeriodCommandResponse.AccountPeriodStats;
import com.batrobot.orchestration.application.dto.response.StatsPeriodCommandResponse.HeroStats;
import com.batrobot.orchestration.application.dto.response.StatsPeriodCommandResponse.LobbyStats;
import com.batrobot.orchestration.application.dto.response.StatsPeriodCommandResponse.PositionStats;
import com.batrobot.shared.application.port.config.AppDayTimeConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Formatter for period stats commands.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StatsPeriodResultFormatter extends BaseResultFormatter {

    private static final String TEMPLATE_NAME = "cmd-stats-period";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM");

    private final MessageSource messageSource;
    private final TelegramTemplateRenderer templateRenderer;
    private final AppDayTimeConfig dayTimeConfig;

    public String formatResult(StatsPeriodCommandResponse response, String languageCode, String headerKey) {
        Locale locale = resolveLocale(languageCode);
        ZoneId zoneId = ZoneId.of(dayTimeConfig.getTimezone());

        String start = Instant.ofEpochSecond(response.periodStartEpoch())
                .atZone(zoneId)
                .toLocalDate()
                .format(DATE_FORMATTER);
        String end = Instant.ofEpochSecond(response.periodEndEpoch())
                .atZone(zoneId)
                .toLocalDate()
                .format(DATE_FORMATTER);

        List<Map<String, Object>> accounts = response.accounts().stream()
                .map(this::buildAccountModel)
                .toList();

        Map<String, Object> model = new HashMap<>();
        model.put("header", message(headerKey, locale, start, end));
        model.put("labels", labels(locale));
        model.put("dash", message("stats_period.template.dash", locale));
        model.put("accounts", accounts);

        return templateRenderer.render(TEMPLATE_NAME, model);
    }

    private Map<String, Object> buildAccountModel(AccountPeriodStats account) {
        Map<String, Object> model = new HashMap<>();
        model.put("steamUsername", account.steamUsername());
        model.put("totalMatches", account.totalMatches());

        model.put("lobbies", withBranch(account.lobbyStats().stream()
                .map(this::buildLobbyModel)
                .toList()));

        model.put("averageDuration", formatDuration(account.durationStats().averageDurationSeconds()));
        model.put("fastestDuration", formatDuration(account.durationStats().fastestDurationSeconds()));
        model.put("fastestUrl", account.durationStats().fastestMatchId() != null
                ? STRATZ_MATCH_URL + account.durationStats().fastestMatchId()
                : null);
        model.put("longestDuration", formatDuration(account.durationStats().longestDurationSeconds()));
        model.put("longestUrl", account.durationStats().longestMatchId() != null
                ? STRATZ_MATCH_URL + account.durationStats().longestMatchId()
                : null);

        model.put("currentStreak", account.streakStats().currentStreak());
        model.put("currentResultIcon",
                account.streakStats().currentIsVictory() == null ? "⚪"
                        : formatResult(account.streakStats().currentIsVictory()));
        model.put("maxWinStreak", account.streakStats().maxWinStreak());
        model.put("maxLossStreak", account.streakStats().maxLossStreak());

        model.put("averageImp", account.performanceStats().averageImp());
        model.put("kdaRatio", formatOneDecimal(account.performanceStats().kdaRatio()));
        model.put("totalKills", account.performanceStats().totalKills());
        model.put("totalDeaths", account.performanceStats().totalDeaths());
        model.put("totalAssists", account.performanceStats().totalAssists());
        model.put("averageGpm", account.performanceStats().averageGpm());
        model.put("averageXpm", account.performanceStats().averageXpm());
        model.put("averageLastHits", formatOneDecimal(account.performanceStats().averageLastHits()));
        model.put("averageDenies", formatOneDecimal(account.performanceStats().averageDenies()));
        model.put("averageHeroDamage", account.performanceStats().averageHeroDamage());
        model.put("averageTowerDamage", account.performanceStats().averageTowerDamage());
        model.put("averageObserverWardsPurchased",
                formatOneDecimal(account.performanceStats().averageObserverWardsPurchased()));
        model.put("averageObserverWardsDestroyed",
                formatOneDecimal(account.performanceStats().averageObserverWardsDestroyed()));
        model.put("averageSentryWardsPurchased",
                formatOneDecimal(account.performanceStats().averageSentryWardsPurchased()));
        model.put("averageSentryWardsDestroyed",
                formatOneDecimal(account.performanceStats().averageSentryWardsDestroyed()));
        model.put("averageHeroHealing", account.performanceStats().averageHeroHealing());

        model.put("positions", withBranch(account.positionStats().stream()
                .map(this::buildPositionModel)
                .toList()));

        model.put("heroes", withBranch(account.topHeroes().stream()
                .map(this::buildHeroModel)
                .toList()));

        model.put("showMvpCount", account.achievementStats().mvpCount() != null
                && account.achievementStats().mvpCount() > 0);
        model.put("mvpCount", account.achievementStats().mvpCount());
        model.put("showTopCoreCount", account.achievementStats().topCoreCount() != null
                && account.achievementStats().topCoreCount() > 0);
        model.put("topCoreCount", account.achievementStats().topCoreCount());
        model.put("showTopSupportCount", account.achievementStats().topSupportCount() != null
                && account.achievementStats().topSupportCount() > 0);
        model.put("topSupportCount", account.achievementStats().topSupportCount());
        model.put("totalImp", formatImp(account.achievementStats().totalImp()));

        return model;
    }

    private Map<String, Object> buildLobbyModel(LobbyStats lobby) {
        Map<String, Object> model = new HashMap<>();
        model.put("lobbyIcon", formatLobbyType(lobby.lobbyType()));
        model.put("matches", lobby.matches());
        model.put("resultIcon", resultIcon(lobby.winRatePercent()));
        model.put("winRate", lobby.winRatePercent());
        model.put("wins", lobby.wins());
        model.put("losses", lobby.losses());
        return model;
    }

    private Map<String, Object> buildPositionModel(PositionStats position) {
        Map<String, Object> model = new HashMap<>();
        model.put("positionIcon", formatPosition(position.position()));
        model.put("gameResultIcon", resultIcon(position.gameWinRatePercent()));
        model.put("gameWinRate", position.gameWinRatePercent());
        model.put("laneResultIcon", resultIcon(position.laneWinRatePercent()));
        model.put("laneWinRate", position.laneWinRatePercent());
        return model;
    }

    private Map<String, Object> buildHeroModel(HeroStats hero) {
        Map<String, Object> model = new HashMap<>();
        model.put("heroName", hero.heroName());
        model.put("matches", hero.matches());
        model.put("resultIcon", resultIcon(hero.winRatePercent()));
        model.put("winRate", hero.winRatePercent());
        return model;
    }

    private List<Map<String, Object>> withBranch(List<Map<String, Object>> items) {
        for (int i = 0; i < items.size(); i++) {
            items.get(i).put("branch", i == items.size() - 1 ? "└" : "├");
        }
        return items;
    }

    private Map<String, Object> labels(Locale locale) {
        Map<String, Object> labels = new HashMap<>();
        labels.put("pubs", message("stats_period.template.pubs", locale));
        labels.put("duration", message("stats_period.template.duration", locale));
        labels.put("fastest", message("stats_period.template.fastest", locale));
        labels.put("longest", message("stats_period.template.longest", locale));
        labels.put("streaks", message("stats_period.template.streaks", locale));
        labels.put("wins", message("stats_period.template.wins", locale));
        labels.put("losses", message("stats_period.template.losses", locale));
        labels.put("performance", message("stats_period.template.performance", locale));
        labels.put("kda", message("stats_period.template.kda", locale));
        labels.put("gpm", message("stats_period.template.gpm", locale));
        labels.put("xpm", message("stats_period.template.xpm", locale));
        labels.put("lastHits", message("stats_period.template.last_hits", locale));
        labels.put("damage", message("stats_period.template.damage", locale));
        labels.put("heroDamage", message("stats_period.template.hero_damage", locale));
        labels.put("towerDamage", message("stats_period.template.tower_damage", locale));
        labels.put("wards", message("stats_period.template.wards", locale));
        labels.put("observer", message("stats_period.template.observer", locale));
        labels.put("sentry", message("stats_period.template.sentry", locale));
        labels.put("healing", message("stats_period.template.healing", locale));
        labels.put("positions", message("stats_period.template.positions", locale));
        labels.put("heroes", message("stats_period.template.heroes", locale));
        labels.put("achievements", message("stats_period.template.achievements", locale));
        labels.put("mvp", message("stats_period.template.mvp", locale));
        labels.put("topCore", message("stats_period.template.top_core", locale));
        labels.put("topSupport", message("stats_period.template.top_support", locale));
        labels.put("imp", message("stats_period.template.imp", locale));
        return labels;
    }

    private String message(String key, Locale locale, Object... args) {
        return messageSource.getMessage(key, args, locale);
    }
}
