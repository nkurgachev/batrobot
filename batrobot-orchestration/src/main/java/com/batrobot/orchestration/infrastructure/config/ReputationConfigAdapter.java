package com.batrobot.orchestration.infrastructure.config;

import com.batrobot.orchestration.application.port.config.ReputationConfig;

import java.util.Map;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Adapter that implements ReputationConfig using Spring configuration properties.
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.reputation")
public class ReputationConfigAdapter implements ReputationConfig {
    private Map<String, Integer> fixedReputations = Map.of();

    public void setFixedReputations(String ignored) {
        this.fixedReputations = Map.of();
    }
}
