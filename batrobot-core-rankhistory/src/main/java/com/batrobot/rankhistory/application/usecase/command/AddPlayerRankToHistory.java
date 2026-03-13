package com.batrobot.rankhistory.application.usecase.command;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.batrobot.rankhistory.application.dto.request.PlayerRankRequest;
import com.batrobot.rankhistory.application.dto.response.PlayerRankResponse;
import com.batrobot.rankhistory.application.mapper.PlayerRankHistoryMapper;
import com.batrobot.rankhistory.domain.model.PlayerRankHistory;
import com.batrobot.rankhistory.domain.repository.PlayerRankHistoryRepository;

/**
 * UseCase for adding a new player rank to history.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class AddPlayerRankToHistory {
    
    private final PlayerRankHistoryRepository rankHistoryRepository;
    private final PlayerRankHistoryMapper rankMapper;
    
    /**
     * Creates a new PlayerRankHistory domain entity and persists it.
     * 
     * @param request Player rank request containing steamId and seasonRank
     * @return Persisted rank history as response DTO
     */
    @Transactional
    public PlayerRankResponse execute(@Valid PlayerRankRequest request) {
        // 1. Convert request DTO to domain entity
        PlayerRankHistory newPlayerRank = rankMapper.createFromRequest(request);
        
        // 2. Persist the domain entity
        PlayerRankHistory savedPlayerRank = rankHistoryRepository.save(newPlayerRank);
        log.trace("Added rank history for player {} with seasonRank {}", 
            request.getSteamId64(), request.getSeasonRank());
        
        // 3. Convert domain entity to response DTO
        return rankMapper.toResponse(savedPlayerRank);
    }
}

