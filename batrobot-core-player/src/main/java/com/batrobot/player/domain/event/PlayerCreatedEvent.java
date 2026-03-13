package com.batrobot.player.domain.event;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.batrobot.shared.domain.event.DomainEvent;
import com.batrobot.shared.domain.model.valueobject.SeasonRank;
import com.batrobot.shared.domain.model.valueobject.SteamId;

public record PlayerCreatedEvent(
        UUID id,
        SteamId steamId,
        String steamUsername,
        String profileUrl,
        String avatarUrl,
        Long accountCreationDate,
        Integer communityVisibleState,
        Boolean isAnonymous,
        Boolean isStratzPublic,
        Boolean isDotaPlusSubscriber,
        Integer smurfFlag,
        SeasonRank seasonRank,
        String activity,
        Integer imp,
        Integer matchCount,
        Integer winCount,
        Long firstMatchDate,
        Long lastMatchDate,

        OffsetDateTime occurredAt)
        implements DomainEvent {

    public PlayerCreatedEvent(
            UUID id,
            SteamId steamId,
            String steamUsername,
            String profileUrl,
            String avatarUrl,
            Long accountCreationDate,
            Integer communityVisibleState,
            Boolean isAnonymous,
            Boolean isStratzPublic,
            Boolean isDotaPlusSubscriber,
            Integer smurfFlag,
            SeasonRank seasonRank,
            String activity,
            Integer imp,
            Integer matchCount,
            Integer winCount,
            Long firstMatchDate,
            Long lastMatchDate) {
                this(
                    id,
                    steamId,
                    steamUsername,
                    profileUrl,
                    avatarUrl,
                    accountCreationDate,
                    communityVisibleState,
                    isAnonymous,
                    isStratzPublic,
                    isDotaPlusSubscriber,
                    smurfFlag,
                    seasonRank,
                    activity,
                    imp,
                    matchCount,
                    winCount,
                    firstMatchDate,
                    lastMatchDate,
                    OffsetDateTime.now());
    }

    @Override
    public Object aggregateId() {
        return id;
    }

    @Override
    public String eventType() {
        return "player.created";
    }
}

