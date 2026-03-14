package com.batrobot.bot.infrastructure.telegram.formatter.command;

import com.batrobot.bot.infrastructure.telegram.formatter.base.BaseResultFormatter;
import com.batrobot.bot.infrastructure.telegram.formatter.base.TelegramTemplateRenderer;
import com.batrobot.orchestration.application.dto.response.AllPubsTodayCommandResponse;
import com.batrobot.orchestration.application.dto.response.AllPubsTodayCommandResponse.UserMatchHistory;
import com.batrobot.orchestration.application.dto.response.AllPubsTodayCommandResponse.UserMatchHistory.PlayerMatchHistory;
import com.batrobot.orchestration.application.dto.response.AllPubsTodayCommandResponse.UserMatchHistory.PlayerMatchHistory.MatchStats;
import com.batrobot.shared.application.port.config.AppDayTimeConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Formatter for match results.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AllPubsTodayResultFormatter extends BaseResultFormatter {

    private static final String STRATZ_MATCH_URL = "https://stratz.com/matches/";
    private static final String TEMPLATE_NAME = "cmd-all-pubs-today";
    private static final String MESSAGE_KEY_HEADER = "all_pubs_today.template.header";

    private final MessageSource messageSource;
    private final TelegramTemplateRenderer templateRenderer;
    private final AppDayTimeConfig dayTimeConfig;

    /**
     * Formats matches grouped by Telegram user with HTML formatting.
     *
     * @param response     response containing user match data
     * @param languageCode telegram language code
     * @return formatted HTML message string
     */
    public String formatResult(AllPubsTodayCommandResponse response, String languageCode) {
        log.debug("Formatting matches for {} users", response.userMatches().size());

        List<Map<String, Object>> userMatches = response.userMatches().stream()
                .map(this::buildUserModel)
                .toList();

        Map<String, Object> model = Map.of(
                "header", messageSource.getMessage(MESSAGE_KEY_HEADER, null, resolveLocale(languageCode)),
                "userMatches", userMatches);
        return templateRenderer.render(TEMPLATE_NAME, model);
    }

    private Map<String, Object> buildUserModel(UserMatchHistory userGroup) {
        List<Map<String, Object>> players = IntStream.range(0, userGroup.players().size())
                .mapToObj(i -> buildPlayerModel(userGroup.players().get(i), i + 1))
                .toList();

        return Map.of(
                "emoji", resolveEmoji(userGroup.emoji()),
                "fullName", formatFullName(userGroup.firstName(), userGroup.lastName(), userGroup.telegramUsername()),
                "players", players);
    }

    private Map<String, Object> buildPlayerModel(PlayerMatchHistory player, Integer index) {
        long wins = player.matches().stream()
                .filter(m -> Boolean.TRUE.equals(m.isVictory()))
                .count();

        List<Map<String, Object>> matches = player.matches().stream()
                .map(this::buildMatchModel)
                .toList();

        return Map.of(
                "index", index,
                "steamUsername", player.steamUsername(),
                "winCount", wins,
                "matchCount", player.matches().size(),
                "matches", matches);
    }

    private Map<String, Object> buildMatchModel(MatchStats match) {
        String matchTime = formatDateTime(match.startDateTime(), dayTimeConfig.getTimezone());
        String matchUrl = STRATZ_MATCH_URL + match.matchId();
        String result = formatResult(match.isVictory());
        String heroName = match.heroName() != null ? match.heroName() : "Unknown";

        int kills = match.kills() != null ? match.kills() : 0;
        int deaths = match.deaths() != null ? match.deaths() : 0;
        int assists = match.assists() != null ? match.assists() : 0;
        String kda = String.format("%d/%d/%d", kills, deaths, assists);
        String lobbyType = match.lobbyType() != null ? formatLobbyType(match.lobbyType()) : "";
        String gameMode = match.gameMode() != null ? formatGameMode(match.gameMode()) : "";

        Map<String, Object> model = new LinkedHashMap<>();
        model.put("lobbyType", lobbyType);
        model.put("gameMode", gameMode);
        model.put("matchTime", matchTime);
        model.put("matchUrl", matchUrl);
        model.put("result", result);
        model.put("heroName", heroName);
        model.put("kda", kda);

        if (match.position() != null) {
            model.put("position", formatPosition(match.position()));
        }

        if (match.award() != null && !match.award().equals("NONE")) {
            model.put("award", formatAward(match.award()));
        }

        if (match.imp() != null) {
            model.put("imp", formatImp(match.imp()));
        }

        return model;
    }
}
