package com.batrobot.ingestion.infrastructure.config;

import com.batrobot.ingestion.application.port.config.IngestionStratzSyncConfig;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Infrastructure config binding for Stratz ingestion sync settings.
 */
@Data
@Component
@ConfigurationProperties(prefix = "ingestion.stratz.sync")
public class IngestionStratzSyncProperties implements IngestionStratzSyncConfig {

    private long historicalStartTimestamp;

    private int matchesLimit;
}
