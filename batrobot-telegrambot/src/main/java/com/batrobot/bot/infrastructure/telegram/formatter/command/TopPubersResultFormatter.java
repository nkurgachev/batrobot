package com.batrobot.bot.infrastructure.telegram.formatter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.batrobot.bot.infrastructure.telegram.formatter.base.BaseResultFormatter;
import com.batrobot.bot.infrastructure.telegram.formatter.base.TelegramTemplateRenderer;
import com.batrobot.orchestration.application.dto.response.TopPubersResponse;
import com.batrobot.orchestration.application.dto.response.TopPubersResponse.PuberInfo;
import com.batrobot.orchestration.application.dto.response.TopPubersResponse.RankGroup;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Formatter for top_pubers command results.
 * Renders pre-grouped player data via Mustache template.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TopPubersResultFormatter extends BaseResultFormatter {

    private static final String TEMPLATE_NAME = "cmd-top-pubers";
    private static final String MESSAGE_KEY_HEADER = "top_pubers.template.header";

    private final MessageSource messageSource;
    private final TelegramTemplateRenderer templateRenderer;

    /**
     * Formats all chat members grouped by seasonal rank.
     *
     * @param response     pre-grouped pubers by seasonal rank
     * @param languageCode telegram language code
     * @return formatted HTML message string
     */
    public String formatPubers(TopPubersResponse response, String languageCode) {
        Locale locale = resolveLocale(languageCode);
        log.debug("Formatting top pubers for {} groups", response.rankGroups().size());

        List<Map<String, Object>> groups = response.rankGroups().stream()
                .map(this::buildRankGroupModel)
                .toList();

        Map<String, Object> model = Map.of(
                "header", messageSource.getMessage(MESSAGE_KEY_HEADER, null, locale),
                "rankGroups", groups);

        String result = templateRenderer.render(TEMPLATE_NAME, model);
        log.debug("Successfully formatted top pubers");
        return result;
    }

    private Map<String, Object> buildRankGroupModel(RankGroup group) {
        List<Map<String, Object>> players = IntStream.range(0, group.players().size())
                .mapToObj(i -> buildPlayerModel(group.players().get(i), i + 1))
                .toList();

        return Map.of(
                "medal", formatRankAsMedal(group.seasonRank(), messageSource, null),
                "players", players);
    }

    private Map<String, Object> buildPlayerModel(PuberInfo puber, Integer index) {
        return Map.of(
                "index", index,
                "emoji", resolveEmoji(puber.emoji()),
                "fullName", formatFullName(puber.firstName(), puber.lastName(), puber.telegramUsername()),
                "steamUsername", puber.steamUsername() != null ? puber.steamUsername() : "unknown");
    }

}
