package com.batrobot.bot.infrastructure.telegram.formatter.base;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Renders Telegram HTML messages from Mustache templates.
 * Templates are loaded from classpath: templates/telegram/{name}.mustache
 */
@Component
public class TelegramTemplateRenderer {

    private final Mustache.Compiler compiler;
    private final Map<String, Template> cache = new ConcurrentHashMap<>();

    public TelegramTemplateRenderer() {
        this.compiler = Mustache.compiler()
                .escapeHTML(true)
                .defaultValue("");
    }

    /**
     * Renders a template with the given model variables.
     *
     * @param templateName template name without prefix/suffix (e.g. "me-command")
     * @param model        variables accessible in the template via {{key}}
     * @return rendered Telegram HTML message, trimmed
     */
    public String render(String templateName, Map<String, Object> model) {
        Template template = cache.computeIfAbsent(templateName, this::loadTemplate);
        return template.execute(model).trim();
    }

    private Template loadTemplate(String name) {
        String path = "templates/telegram/" + name + ".mustache";
        try (Reader reader = new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(path),
                StandardCharsets.UTF_8)) {
            return compiler.compile(reader);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load template: " + path, e);
        }
    }
}
