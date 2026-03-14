package com.batrobot.shared.application.port.config;

/**
 * Shared app day-time configuration contract.
 */
public interface AppDayTimeConfig {

    /**
     * Hour when the day starts (0-23).
     */
    int getStartHour();

    /**
     * Timezone for day calculations.
     */
    String getTimezone();
}