package com.batrobot.bot.infrastructure.telegram.formatter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.batrobot.bot.infrastructure.telegram.formatter.base.BaseResultFormatter;
import com.batrobot.bot.infrastructure.telegram.formatter.base.TelegramTemplateRenderer;
import com.batrobot.orchestration.application.dto.response.MeCommandResponse;
import com.batrobot.orchestration.application.dto.response.MeCommandResponse.PlayerRankHistory;
import com.batrobot.orchestration.application.dto.response.MeCommandResponse.PlayerRankHistory.RankInfo;
import com.batrobot.shared.application.port.config.AppDayTimeConfig;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Formatter for me command results.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MeResultFormatter extends BaseResultFormatter {

    private static final String TEMPLATE_NAME = "cmd-me";
    private static final String MESSAGE_KEY_HEADER = "me.template.header";
    private static final String MESSAGE_KEY_NO_RANK = "format.match.medal.0";
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    private final MessageSource messageSource;
    private final TelegramTemplateRenderer templateRenderer;
    private final AppDayTimeConfig dayTimeConfig;

    /**
     * Formats list of user's Steam accounts into a user-friendly message
     *
     * @param response     response containing the list of Steam accounts with rank
     *                     history for the user
     * @param languageCode telegram language code
     * @return formatted HTML message string
     */
    public String formatResult(MeCommandResponse response, String languageCode) {
        log.debug("Formatting me results for {} accounts", response.players().size());

        Locale locale = resolveLocale(languageCode);

        List<PlayerRankHistory> playerList = response.players();
        List<Map<String, Object>> players = IntStream.range(0, playerList.size())
                .mapToObj(i -> buildPlayerModel(playerList.get(i), i + 1, locale))
                .toList();

        Map<String, Object> model = Map.of(
                "header", messageSource.getMessage(MESSAGE_KEY_HEADER, null, locale),
                "noRankMessage", messageSource.getMessage(MESSAGE_KEY_NO_RANK, null, locale),
                "players", players);

        String result = templateRenderer.render(TEMPLATE_NAME, model);

        log.debug("Successfully formatted me results");
        return result;
    }

    private Map<String, Object> buildPlayerModel(PlayerRankHistory player, Integer index, Locale locale) {
        List<RankInfo> rankHistory = player.rankHistory() != null ? player.rankHistory() : List.of();

        List<Map<String, Object>> rankEntries = new ArrayList<>();
        for (int i = 0; i < rankHistory.size(); i++) {
            RankInfo current = rankHistory.get(i);
            RankInfo previous = i < rankHistory.size() - 1 ? rankHistory.get(i + 1) : null;
            rankEntries.add(buildRankEntry(current, previous, locale));
        }

        return Map.of(
                "index", index,
                "steamUsername", player.steamUsername(),
                "steamId64", player.steamId64(),
                "rankEntries", rankEntries,
                "hasRankHistory", !rankEntries.isEmpty());
    }

    private Map<String, Object> buildRankEntry(RankInfo current, RankInfo previous, Locale locale) {
        String round = "⚪️";
        String arrow = "➡️";

        if (previous != null && current.seasonRank() != null && previous.seasonRank() != null) {
            if (current.seasonRank() > previous.seasonRank()) {
                arrow = "⬆️";
                round = "🟢";
            } else if (current.seasonRank() < previous.seasonRank()) {
                arrow = "⬇️";
                round = "🔴";
            }
        }

        String date = formatDate(current.assignedAt(), dayTimeConfig.getTimezone(), ISO_DATE);
        String medal = formatRankAsMedal(current.seasonRank(), messageSource, locale);

        return Map.of(
                "arrow", arrow,
                "round", round,
                "date", date,
                "medal", medal);
    }
}
