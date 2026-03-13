package com.batrobot.match.application.usecase.command;

import com.batrobot.match.application.dto.request.MatchRequest;
import com.batrobot.match.application.dto.response.MatchResponse;
import com.batrobot.match.application.mapper.MatchMapper;
import com.batrobot.match.domain.repository.MatchRepository;
import com.batrobot.shared.domain.model.valueobject.MatchId;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * UseCase for upserting a Match entity.
 * Creates new match if not exists, updates if info changed.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class UpsertMatch {

    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;

    /**
     * Upserts a Match based on MatchRequest.
     * Creates new match if not exists, updates if info changed.
     * Single database operation using repository.
     * 
     * @param request Match request with match data
     * @return Match response
     */
    @Transactional
    public MatchResponse execute(@Valid MatchRequest request) {
        log.debug("Upserting match: matchId={}", request.getMatchId());

        MatchId matchId = MatchId.of(request.getMatchId());

        return matchRepository.findByMatchId(matchId)
                .map(existing -> {
                    boolean hasChanges = false;

                    hasChanges |= existing.updateTimings(
                            request.getStartDateTime(),
                            request.getEndDateTime(),
                            request.getDurationSeconds());

                    hasChanges |= existing.updateGameInfo(
                            request.getGameMode(),
                            request.getLobbyType());

                    hasChanges |= existing.updateOutcome(
                            request.getActualRank(),
                            request.getRadiantKills(),
                            request.getDireKills());

                    hasChanges |= existing.updateLaneAnalysis(
                            request.getAnalysisOutcome(),
                            request.getBottomLaneOutcome(),
                            request.getMidLaneOutcome(),
                            request.getTopLaneOutcome());

                    if (hasChanges) {
                        matchRepository.save(existing);
                        log.debug("Updated match info for matchId {}", matchId.value());
                    }
                    return matchMapper.toResponse(existing);
                })
                .orElseGet(() -> {
                    log.debug("Creating new match: matchId={}", matchId.value());
                    var newMatch = matchMapper.createFromRequest(request);
                    var savedMatch = matchRepository.save(newMatch);
                    return matchMapper.toResponse(savedMatch);
                });
    }
}

