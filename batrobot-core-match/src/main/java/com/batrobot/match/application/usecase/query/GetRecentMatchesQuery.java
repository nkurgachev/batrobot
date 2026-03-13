package com.batrobot.match.application.usecase.query;

import com.batrobot.match.application.dto.response.MatchResponse;
import com.batrobot.match.application.mapper.MatchMapper;
import com.batrobot.match.domain.repository.MatchRepository;

import jakarta.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Query Use Case for fetching matches that started after a given timestamp.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class GetRecentMatchesQuery {

    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;

    /**
     * Fetches matches with startDateTime >= startTime.
     *
     * @param startTime Unix timestamp (seconds)
     * @return List of MatchResponse
     */
    @Transactional(readOnly = true)
    public List<MatchResponse> execute(@NotNull Long startTime) {
        log.debug("Fetching matches since timestamp {}", startTime);

        return matchRepository.findRecentMatches(startTime).stream()
                .map(matchMapper::toResponse)
                .toList();
    }
}
