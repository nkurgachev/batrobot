package com.batrobot.orchestration.application.port.config;

/**
 * Port interface for day timing configuration.
 */
public interface DayTimeConfig {
    
    /**
     * Hour when the day starts (0-23).
     */
    int getStartHour();
    
    /**
     * Timezone for day calculations.
     */
    String getTimezone();
}
