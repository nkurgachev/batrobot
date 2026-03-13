package com.batrobot.stratz.infrastructure.adapter;

import com.batrobot.stratz.application.dto.response.StratzPlayerResponse;
import com.batrobot.stratz.application.mapper.StratzPlayerMapper;
import com.batrobot.stratz.application.port.StratzPlayersGatewayPort;
import com.batrobot.stratz.infrastructure.StratzQueryExecutor;
import com.batrobot.stratz.infrastructure.projection.PlayersProjection;

import com.batrobot.shared.domain.model.valueobject.SteamId;

import com.batrobot.stratz.generated.client.PlayersGraphQLQuery;
import com.batrobot.stratz.generated.client.PlayersProjectionRoot;
import com.batrobot.stratz.generated.types.PlayerType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Infrastructure adapter implementing StratzPlayersGatewayPort.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StratzPlayersGatewayAdapter implements StratzPlayersGatewayPort {

    private final StratzQueryExecutor stratzQueryExecutor;

    private final ObjectMapper objectMapper;
    private final StratzPlayerMapper stratzPlayerMapper;

    /**
     * Fetches players from Stratz API.
     * 
     * @param steamIds64 List of 64-bit Steam IDs to fetch
     * @return List of StratzPlayerResponse in the same order as input
     */
    @Override
    public List<StratzPlayerResponse> fetchPlayers(List<Long> steamIds64) {
        log.debug("Fetching players from Stratz for {} ids", steamIds64 != null ? steamIds64.size() : 0);

        if (steamIds64 == null || steamIds64.isEmpty()) {
            return List.of();
        }

        // Convert primitives to Value Objects
        List<SteamId> steamIdObjects = steamIds64.stream()
                .map(SteamId::fromSteamId64)
                .toList();
        return fetchPlayersFromStratzBatch(steamIdObjects);
    }

    /**
     * Fetches players from Stratz API in batch mode.
     * Orchestrates the full flow: query building, execution, parsing, and mapping.
     * 
     * @param steamIds List of Steam IDs to fetch
     * @return List of StratzPlayerResponse
     */
    private List<StratzPlayerResponse> fetchPlayersFromStratzBatch(List<SteamId> steamIds) {
        // Build and execute GraphQL query
        JsonNode data = executeGraphQLQuery(steamIds);
        JsonNode playersNode = data != null ? data.get("players") : null;

        if (playersNode == null || !playersNode.isArray()) {
            log.warn("No players node returned from Stratz for ids {}. Response: {}", steamIds, data);
            return List.of();
        }

        // Parse and map each player
        List<StratzPlayerResponse> results = new ArrayList<>();
        for (JsonNode playerNode : playersNode) {
            try {
                StratzPlayerResponse response = parseAndMapPlayer(playerNode);
                if (response != null) {
                    results.add(response);
                }
            } catch (Exception e) {
                log.error("Failed to parse or map player node: {}. Error: ", playerNode, e);
            }
        }

        return results;
    }

    /**
     * Executes the GraphQL query against Stratz API.
     * 
     * @param steamIds List of Steam IDs to query
     * @return JsonNode with response data
     */
    private JsonNode executeGraphQLQuery(List<SteamId> steamIds) {
        // Convert 64-bit Steam IDs to 32-bit format for Stratz API
        List<Long> steamIds32 = steamIds.stream()
                .map(SteamId::toSteamId32)
                .toList();

        log.info("Steam ID conversion: {} (64-bit) → {} (32-bit for Stratz API)", steamIds, steamIds32);

        // Build query and projection
        PlayersGraphQLQuery query = PlayersGraphQLQuery.newRequest()
                .steamAccountIds(steamIds32)
                .queryName("GetPlayers")
                .build();

        PlayersProjectionRoot<?, ?> projection = PlayersProjection.buildPlayersProjection();
        GraphQLQueryRequest request = new GraphQLQueryRequest(query, projection);
        String graphQLQuery = request.serialize();

        Map<String, Object> variables = new HashMap<>();
        variables.put("steamIds", steamIds32);

        return stratzQueryExecutor.executeQuery(graphQLQuery, variables);
    }

    /**
     * Parses and maps a single player node from API response.
     * 
     * @param playerNode JsonNode representing a player
     * @return StratzPlayerResponse or null if parsing failed
     * @throws Exception if deserialization fails
     */
    private StratzPlayerResponse parseAndMapPlayer(JsonNode playerNode) throws Exception {
        log.debug("Processing player node: {}", playerNode);
        
        PlayerType player = objectMapper.treeToValue(playerNode, PlayerType.class);
        if (player == null || player.getSteamAccount() == null || player.getSteamAccount().getId() == null) {
            log.warn("Skipping player entry without steamAccountId. Player node: {}", playerNode);
            return null;
        }

        long steamId32 = player.getSteamAccount().getId();
        log.debug("Player steamId32 from Stratz: {}", steamId32);

        StratzPlayerResponse response = stratzPlayerMapper.toStratzPlayerResponse(player);
        log.info("Successfully mapped player: steamId32={} → steamId64={}", steamId32, response.getSteamId64());
        
        return response;
    }
}

