package com.batrobot.orchestration.infrastructure.config;

import com.batrobot.orchestration.application.port.config.DayTimeConfig;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Adapter that implements DayTimeConfigPort using Spring configuration properties.
 * 
 * This allows the Application layer to depend on the port interface instead of
 * on Infrastructure configuration, maintaining layer isolation.
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.day")
public class DayTimeConfigAdapter implements DayTimeConfig {
    
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
