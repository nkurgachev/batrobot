package com.batrobot.ingestion.application.port.config;

/**
 * Application-level view of ingestion sync settings.
 */
public interface IngestionStratzSyncConfig {

    long getHistoricalStartTimestamp();

    int getMatchesLimit();
}
