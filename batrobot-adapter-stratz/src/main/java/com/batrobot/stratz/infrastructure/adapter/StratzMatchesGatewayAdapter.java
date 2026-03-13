package com.batrobot.stratz.infrastructure.adapter;

import com.batrobot.stratz.application.dto.response.StratzMatchesResponse;
import com.batrobot.stratz.application.mapper.StratzMatchMapper;
import com.batrobot.stratz.application.port.StratzMatchesGatewayPort;
import com.batrobot.stratz.infrastructure.StratzQueryExecutor;
import com.batrobot.stratz.infrastructure.projection.MatchAndPlayerStatsProjection;

import com.batrobot.shared.domain.model.valueobject.SteamId;

import io.github.nkurgachev.stratz.generated.client.PlayerGraphQLQuery;
import io.github.nkurgachev.stratz.generated.client.PlayerProjectionRoot;
import io.github.nkurgachev.stratz.generated.types.PlayerType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Infrastructure adapter implementing StratzMatchesGatewayPort.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StratzMatchesGatewayAdapter implements StratzMatchesGatewayPort {

    private final StratzQueryExecutor stratzQueryExecutor;

    private final ObjectMapper objectMapper;
    private final StratzMatchMapper stratzMatchMapper;

    /**
     * Fetches matches for a single player from Stratz API.
     *
     * @param steamId64 Player's 64-bit Steam ID
     * @param startTime Unix timestamp (seconds) — fetch matches after this time
     * @param take Maximum number of matches to fetch
     * @return StratzMatchesResponse with match list (can be empty)
     */
    @Override
    public StratzMatchesResponse fetchMatches(Long steamId64, long startTime, int take) {
        JsonNode data = executeGraphQLQuery(steamId64, startTime, take);
        JsonNode playerNode = data != null ? data.get("player") : null;

        if (playerNode == null || playerNode.isNull()) {
            log.debug("No player data found for steamId {}", steamId64);
            return new StratzMatchesResponse(steamId64, List.of());
        }

        return parseAndMapResponse(playerNode, steamId64);
    }

    /**
     * Builds and executes GraphQL query against Stratz API.
     *
     * @param steamId64 Player's 64-bit Steam ID
     * @param startTime Unix timestamp (seconds)
     * @param take Maximum number of matches to fetch
     * @return JsonNode with response data
     */
    private JsonNode executeGraphQLQuery(Long steamId64, long startTime, int take) {
        Long steamId32 = SteamId.fromSteamId64(steamId64).toSteamId32();

        log.info("Fetching matches for steamId {} (32-bit: {}) with startTime={}, take={}",
                steamId64, steamId32, startTime, take);

        // Build query and projection
        PlayerGraphQLQuery query = PlayerGraphQLQuery.newRequest()
                .steamAccountId(steamId32)
                .queryName("GetPlayerMatches")
                .build();

        PlayerProjectionRoot<?, ?> projection = MatchAndPlayerStatsProjection.buildPlayerMatchesProjection(
            steamId32, startTime, take);

        GraphQLQueryRequest graphqlRequest = new GraphQLQueryRequest(query, projection);
        String graphQLQuery = graphqlRequest.serialize();

        Map<String, Object> variables = new HashMap<>();
        variables.put("steamAccountId", steamId32);

        return stratzQueryExecutor.executeQuery(graphQLQuery, variables);
    }

    /**
     * Parses and maps player node from API response to intermediate DTOs.
     *
     * @param playerNode JsonNode representing a player with matches
     * @param steamId64 Player's 64-bit Steam ID
     * @return StratzMatchesResponse or empty response if parsing fails
     */
    private StratzMatchesResponse parseAndMapResponse(JsonNode playerNode, Long steamId64) {
        try {
            PlayerType player = objectMapper.treeToValue(playerNode, PlayerType.class);
            return stratzMatchMapper.toStratzMatchesResponse(player, steamId64);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse player data for steamId {}", steamId64, e);
            return new StratzMatchesResponse(steamId64, List.of());
        }
    }
}

