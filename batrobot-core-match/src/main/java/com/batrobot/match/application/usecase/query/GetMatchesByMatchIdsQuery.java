package com.batrobot.match.application.usecase.query;

import com.batrobot.match.application.dto.response.MatchResponse;
import com.batrobot.match.application.mapper.MatchMapper;
import com.batrobot.match.domain.repository.MatchRepository;
import com.batrobot.shared.domain.model.valueobject.MatchId;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Query Use Case for fetching matches by their match IDs (business keys).
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class GetMatchesByMatchIdsQuery {

    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;

    /**
     * Fetches matches by their external match IDs.
     *
     * @param matchIds Collection of external match IDs (Dota 2 match IDs)
     * @return Map of matchId → MatchResponse
     */
    @Transactional(readOnly = true)
    public Map<Long, MatchResponse> execute(@Valid @NotNull Collection<@NotNull Long> matchIds) {
        log.debug("Fetching {} matches by match IDs", matchIds.size());

        return matchIds.stream()
                .map(id -> matchRepository.findByMatchId(MatchId.of(id)).orElse(null))
                .filter(Objects::nonNull)
                .map(matchMapper::toResponse)
                .collect(Collectors.toMap(MatchResponse::matchId, r -> r));
    }
}

