package com.batrobot.shared.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.batrobot.shared.application.port.config.AppDayTimeConfig;

import lombok.Data;

/**
 * Shared adapter that binds app day-time settings from configuration.
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.day")
public class AppDayTimeConfigAdapter implements AppDayTimeConfig {

    /**
     * Hour when the day starts (0-23).
     * Default: 3 (03:00).
     */
    private int startHour = 3;

    /**
     * Timezone for day calculations.
     * Default: Europe/Moscow.
     */
    private String timezone = "Europe/Moscow";
}