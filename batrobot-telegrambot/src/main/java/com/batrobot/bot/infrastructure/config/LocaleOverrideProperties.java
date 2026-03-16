package com.batrobot.bot.infrastructure.config;

import java.util.Locale;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * Configurable per-user locale overrides.
 * Maps Telegram username to language code.
 */
@Data
@Component
@ConfigurationProperties(prefix = "telegram.locale")
public class LocaleOverrideProperties {
    private String defaultLocale = Locale.getDefault().toLanguageTag();
    private Map<String, String> overrides = Map.of();

    public void setOverrides(Map<String, String> overrides) {
        if (overrides == null || overrides.isEmpty()) {
            this.overrides = Map.of();
            return;
        }

        this.overrides = overrides.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().trim().toLowerCase(),
                        Map.Entry::getValue,
                        (left, right) -> right,
                        LinkedHashMap::new));
    }

    public Locale notificationLocale() {
        return Locale.forLanguageTag(defaultLocale);
    }
}
