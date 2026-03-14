package com.batrobot.bot.infrastructure.telegram.formatter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.batrobot.bot.infrastructure.telegram.formatter.base.BaseResultFormatter;
import com.batrobot.bot.infrastructure.telegram.formatter.base.TelegramTemplateRenderer;
import com.batrobot.orchestration.application.dto.response.RepsCommandResponse;
import com.batrobot.orchestration.application.dto.response.RepsCommandResponse.UserReputation;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class RepsResultFormatter extends BaseResultFormatter {

    private final MessageSource messageSource;
    private final TelegramTemplateRenderer templateRenderer;

    private static final String TEMPLATE_NAME = "cmd-reps";
    private static final String MESSAGE_KEY_HEADER = "reps.template.header";

    public String formatReputation(RepsCommandResponse response, String languageCode) {
        log.debug("Formatting reps for {} users", response.users().size());

        Locale locale = resolveLocale(languageCode);

        List<Map<String, Object>> users = IntStream.range(0, response.users().size())
            .mapToObj(i -> buildUserModel(response.users().get(i), i + 1))
                .toList();

        return templateRenderer.render(TEMPLATE_NAME, Map.of(
                "header", messageSource.getMessage(MESSAGE_KEY_HEADER, null, locale),
                "users", users));
    }

    private Map<String, Object> buildUserModel(UserReputation user, Integer index) {
        return Map.of(
                "index", index,
                "emoji", resolveEmoji(user.emoji()),
                "fullName", formatFullName(user.firstName(), user.lastName(), user.telegramUsername()),
                "reputation", formatReputation(user.reputation()));
    }
}
