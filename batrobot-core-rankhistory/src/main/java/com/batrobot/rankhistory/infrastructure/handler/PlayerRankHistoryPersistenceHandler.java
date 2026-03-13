package com.batrobot.rankhistory.infrastructure.handler;

import com.batrobot.player.domain.event.PlayerRankUpdatedEvent;
import com.batrobot.rankhistory.domain.model.PlayerRankHistory;
import com.batrobot.rankhistory.domain.repository.PlayerRankHistoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlayerRankHistoryPersistenceHandler {

    private final PlayerRankHistoryRepository playerRankHistoryRepository;

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveHistoryOnSeasonRankUpdated(PlayerRankUpdatedEvent event) {
        log.debug("Received PlayerRankUpdatedEvent: steamId={}, oldRank={}, newRank={}", 
            event.steamId(), event.oldSeasonRank(), event.newSeasonRank());
        
        PlayerRankHistory rankHistory = PlayerRankHistory.create(
            event.steamId(),
            event.newSeasonRank()
        );
        playerRankHistoryRepository.save(rankHistory);

        log.info("Successfully saved rank history: steamId={}, seasonRank={}, assignedAt={}", 
            rankHistory.getSteamId(), rankHistory.getSeasonRank(), rankHistory.getAssignedAt());
    }
}
