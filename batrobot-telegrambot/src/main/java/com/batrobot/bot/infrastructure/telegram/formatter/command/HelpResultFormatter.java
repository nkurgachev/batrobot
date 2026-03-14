package com.batrobot.bot.infrastructure.telegram.formatter.command;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.batrobot.bot.infrastructure.telegram.command.handler.base.CommandHandler;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandDocumentation;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandHandlerComponent;
import com.batrobot.bot.infrastructure.telegram.formatter.base.BaseResultFormatter;
import com.batrobot.bot.infrastructure.telegram.formatter.base.TelegramTemplateRenderer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Formatter for help command results.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HelpResultFormatter extends BaseResultFormatter {

    private static final String TEMPLATE_NAME = "cmd-help";
    private static final String MESSAGE_KEY_HEADER = "help.template.header";

    private final MessageSource messageSource;
    private final ApplicationContext context;
    private final TelegramTemplateRenderer templateRenderer;

    /**
     * Formats command documentation into a user-friendly help message with HTML formatting.
     *
     * @param languageCode telegram language code
     * @return formatted HTML help message string
     */
    public String formatResult(String languageCode) {
        log.debug("Formatting help documentation");

        Locale locale = resolveLocale(languageCode);

        Map<String, Object> handlers = context.getBeansWithAnnotation(CommandHandlerComponent.class);

        List<Map<String, Object>> commands = handlers.values().stream()
                .filter(CommandHandler.class::isInstance)
                .map(CommandHandler.class::cast)
                .filter(handler -> handler.getClass().isAnnotationPresent(CommandDocumentation.class))
                .sorted(Comparator.comparing(handler ->
                        handler.getClass().getAnnotation(CommandHandlerComponent.class).value()))
            .map(handler -> buildCommandModel(handler, locale))
                .toList();

        Map<String, Object> model = Map.of(
                "header", messageSource.getMessage(MESSAGE_KEY_HEADER, null, locale),
                "commands", commands
        );

        String result = templateRenderer.render(TEMPLATE_NAME, model);
        log.debug("Successfully formatted help documentation");
        return result;
    }

        private Map<String, Object> buildCommandModel(CommandHandler handler, Locale locale) {
        CommandHandlerComponent cmdAnnotation = handler.getClass()
                .getAnnotation(CommandHandlerComponent.class);
        CommandDocumentation docAnnotation = handler.getClass()
                .getAnnotation(CommandDocumentation.class);

        Map<String, Object> model = new LinkedHashMap<>();
        model.put("command", cmdAnnotation.value());
        model.put("description", messageSource.getMessage(docAnnotation.descriptionKey(), null, locale));
        model.put("usageLabel", messageSource.getMessage("doc.usage", null, locale));
        model.put("usage", docAnnotation.usage());

        if (!docAnnotation.example().isEmpty()) {
            model.put("hasExample", true);
            model.put("exampleLabel", messageSource.getMessage("doc.example", null, locale));
            model.put("example", docAnnotation.example());
        }

        if (!docAnnotation.noteKey().isEmpty()) {
            model.put("hasNote", true);
            model.put("noteLabel", messageSource.getMessage("doc.note", null, locale));
            model.put("note", messageSource.getMessage(docAnnotation.noteKey(), null, locale));
        }

        return model;
    }
}
