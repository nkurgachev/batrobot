package com.batrobot.orchestration.infrastructure.config;

import com.batrobot.orchestration.application.port.config.ReputationConfig;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

    public void setFixedReputations(Map<String, Integer> fixedReputations) {
        if (fixedReputations == null || fixedReputations.isEmpty()) {
            this.fixedReputations = Map.of();
            return;
        }

        this.fixedReputations = fixedReputations.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> normalizeUsername(entry.getKey()),
                        Map.Entry::getValue,
                        (left, right) -> right,
                        LinkedHashMap::new));
    }

    public static String normalizeUsername(String username) {
        if (username == null) {
            return "";
        }
        String normalized = username.trim();
        if (normalized.startsWith("@")) {
            normalized = normalized.substring(1);
        }
        return normalized.toLowerCase();
    }
}
