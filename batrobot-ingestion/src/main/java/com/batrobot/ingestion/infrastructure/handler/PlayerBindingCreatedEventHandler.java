package com.batrobot.ingestion.infrastructure.handler;

import com.batrobot.binding.domain.event.PlayerBindingCreatedEvent;
import com.batrobot.ingestion.application.usecase.command.SyncPlayerMatches;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Event handler that starts immediate ingestion after player binding is created.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PlayerBindingCreatedEventHandler {

    private final SyncPlayerMatches syncPlayerMatchesFromStratz;

    @Async
    @TransactionalEventListener
    public void handle(PlayerBindingCreatedEvent event) {
        log.info("Steam binding created: bindingId={}, chatId={}, userId={}, steamId={}, occurredAt={}",
                event.id(),
                event.chatId(),
                event.userId(),
                event.steamId(),
                event.occurredAt());

        syncPlayerMatchesFromStratz.execute(event.steamId().value());
    }
}

