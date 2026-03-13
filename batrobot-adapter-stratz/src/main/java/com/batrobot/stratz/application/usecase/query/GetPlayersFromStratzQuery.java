package com.batrobot.stratz.application.usecase.query;

import com.batrobot.stratz.application.dto.response.StratzPlayerResponse;
import com.batrobot.stratz.application.exception.StratzUnavailableException;
import com.batrobot.stratz.application.port.StratzPlayersGatewayPort;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Query Use Case for fetching players from Stratz API.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class GetPlayersFromStratzQuery {

    private final StratzPlayersGatewayPort stratzPlayersGatewayPort;

    /**
     * Fetches player data from Stratz API for given Steam IDs.
     *
     * @param steamIds List of 64-bit Steam IDs to fetch
     * @return List of StratzPlayerResponse in the same order as input
     * @throws StratzUnavailableException if Stratz API is unavailable
     */
    public List<StratzPlayerResponse> execute(@NotNull List<Long> steamIds)
            throws StratzUnavailableException {
        log.debug("Fetching players from Stratz for {} ids", steamIds != null ? steamIds.size() : 0);

        if (steamIds == null || steamIds.isEmpty()) {
            return List.of();
        }

        return stratzPlayersGatewayPort.fetchPlayers(steamIds);
    }
}


