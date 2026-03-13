package com.batrobot.stratz.infrastructure;

import com.batrobot.stratz.application.exception.StratzUnavailableException;
import com.batrobot.stratz.infrastructure.config.StratzRateLimiterNames;
import com.batrobot.stratz.client.StratzGraphQLClient;
import com.batrobot.stratz.client.exception.StratzAuthException;
import com.batrobot.stratz.client.exception.StratzRateLimitException;
import com.batrobot.stratz.client.exception.StratzServerException;
import com.fasterxml.jackson.databind.JsonNode;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Low-level executor for Stratz GraphQL queries.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StratzQueryExecutor {

    private final StratzGraphQLClient stratzGraphQLClient;

    /**
     * Executes a GraphQL query against Stratz API.
     *
     * @param query GraphQL query string
     * @param variables Query variables (can be null)
     * @return JsonNode containing the "data" field from response
     * @throws StratzUnavailableException if Stratz API call fails for any reason
     */
    @RateLimiter(name = StratzRateLimiterNames.STRATZ_API)
    public JsonNode executeQuery(String query, Map<String, Object> variables) {
        try {
            log.debug("Executing Stratz GraphQL query");
            return stratzGraphQLClient.executeQuery(query, variables);
        } catch (StratzAuthException e) {
            log.error("Stratz API authentication failed", e);
            throw new StratzUnavailableException("Stratz API authentication failed: invalid or missing token", e);
        } catch (StratzRateLimitException e) {
            log.warn("Stratz API rate limit exceeded", e);
            throw new StratzUnavailableException("Stratz API rate limit exceeded", e);
        } catch (StratzServerException e) {
            log.error("Stratz API server error", e);
            throw new StratzUnavailableException("Stratz API is temporarily unavailable", e);
        } catch (Exception e) {
            log.error("Failed to execute Stratz GraphQL query", e);
            throw new StratzUnavailableException("Failed to execute Stratz GraphQL query", e);
        }
    }
}

