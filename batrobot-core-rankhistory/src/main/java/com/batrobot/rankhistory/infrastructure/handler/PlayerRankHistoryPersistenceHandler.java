package com.batrobot.rankhistory.infrastructure.handler;

import com.batrobot.player.domain.event.PlayerCreatedEvent;
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
    public void saveInitialHistoryOnPlayerCreated(PlayerCreatedEvent event) {
        log.debug("Received PlayerCreatedEvent: steamId={}, seasonRank={}",
            event.steamId(), event.seasonRank());

        if (event.seasonRank() == null) {
            log.debug("Skipping initial rank history persistence: steamId={} has no seasonRank", event.steamId());
            return;
        }

        persistRankHistory(event.steamId(), event.seasonRank(), "player.created");
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveHistoryOnSeasonRankUpdated(PlayerRankUpdatedEvent event) {
        log.debug("Received PlayerRankUpdatedEvent: steamId={}, oldRank={}, newRank={}",
            event.steamId(), event.oldSeasonRank(), event.newSeasonRank());

        if (event.newSeasonRank() == null) {
            log.warn("Skipping rank history persistence: steamId={} has null newSeasonRank", event.steamId());
            return;
        }

        persistRankHistory(event.steamId(), event.newSeasonRank(), "player.rank_changed");
    }

    private void persistRankHistory(
            com.batrobot.shared.domain.model.valueobject.SteamId steamId,
            com.batrobot.shared.domain.model.valueobject.SeasonRank seasonRank,
            String sourceEvent) {
        PlayerRankHistory rankHistory = PlayerRankHistory.create(steamId, seasonRank);
        playerRankHistoryRepository.save(rankHistory);

        log.info("Successfully saved rank history: sourceEvent={}, steamId={}, seasonRank={}, assignedAt={}",
            sourceEvent, rankHistory.getSteamId(), rankHistory.getSeasonRank(), rankHistory.getAssignedAt());
    }
}
