package com.batrobot.bot.infrastructure.telegram.formatter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.batrobot.bot.infrastructure.telegram.formatter.base.BaseResultFormatter;
import com.batrobot.bot.infrastructure.telegram.formatter.base.TelegramTemplateRenderer;
import com.batrobot.orchestration.application.dto.response.InGameCommandResponse;
import com.batrobot.orchestration.application.dto.response.InGameCommandResponse.UserGameStatus;
import com.batrobot.orchestration.application.dto.response.InGameCommandResponse.UserGameStatus.GameInfo;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Formatter for ingame command results.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IngameResultFormatter extends BaseResultFormatter {

    private static final String TEMPLATE_NAME = "cmd-ingame";
    private static final String MESSAGE_KEY_HEADER = "ingame.template.header";
    private static final String MESSAGE_KEY_ON = "ingame.template.on";
    private static final String MESSAGE_KEY_PLAYING = "ingame.template.playing";

    private final MessageSource messageSource;
    private final TelegramTemplateRenderer templateRenderer;

    /**
     * Formats list of players currently in-game into a user-friendly message with
     * HTML formatting.
     *
     * @param response     response containing the list of Steam players currently
     *                     in-game
     * @param languageCode telegram language code
     * @return formatted HTML message string
     */
    public String formatResult(InGameCommandResponse response, String languageCode) {
        log.debug("Formatting ingame results for {} users", response.usersInGame().size());

        Locale locale = resolveLocale(languageCode);

        String on = messageSource.getMessage(MESSAGE_KEY_ON, null, locale);
        String playing = messageSource.getMessage(MESSAGE_KEY_PLAYING, null, locale);

        List<Map<String, Object>> users = response.usersInGame().stream()
                .map(u -> buildUserModel(u, on, playing))
                .toList();

        Map<String, Object> model = Map.of(
                "header", messageSource.getMessage(MESSAGE_KEY_HEADER, null, locale),
                "users", users);

        String result = templateRenderer.render(TEMPLATE_NAME, model);
        log.debug("Successfully formatted ingame results");
        return result;
    }

    private Map<String, Object> buildUserModel(UserGameStatus user, String on, String playing) {
        List<Map<String, Object>> games = user.games().stream()
                .map(g -> buildGameModel(g, on, playing))
                .toList();

        return Map.of(
                "emoji", resolveEmoji(user.emoji()),
                "fullName", formatFullName(user.firstName(), user.lastName(), user.telegramUsername()),
                "games", games);
    }

    private Map<String, Object> buildGameModel(GameInfo game, String on, String playing) {
        return Map.of(
                "on", on,
                "playing", playing,
                "steamUsername", game.steamUsername() != null ? game.steamUsername() : "Unknown",
                "gameName", game.gameName() != null ? game.gameName() : "Unknown Game");
    }
}
