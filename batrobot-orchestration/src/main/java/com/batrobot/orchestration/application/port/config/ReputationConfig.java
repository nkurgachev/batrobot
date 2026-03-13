package com.batrobot.orchestration.application.port.config;

import java.util.Map;

/**
 * Port interface for reputation configuration.
 */
public interface ReputationConfig {

    /**
     * Fixed reputation values per Telegram username.
     * If a username is not in the map, reputation is randomized.
     */
    Map<String, Integer> getFixedReputations();
}
