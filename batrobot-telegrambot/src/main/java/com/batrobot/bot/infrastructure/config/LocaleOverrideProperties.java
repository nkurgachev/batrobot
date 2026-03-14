package com.batrobot.bot.infrastructure.config;

import java.util.Locale;
import java.util.Map;

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

    public Locale notificationLocale() {
        return Locale.forLanguageTag(defaultLocale);
    }
}
