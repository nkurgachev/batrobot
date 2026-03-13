package com.batrobot.match.domain.event;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.batrobot.shared.domain.event.DomainEvent;
import com.batrobot.shared.domain.model.valueobject.MatchId;

/**
 * Domain event: Player binding has been created.
 */
public record MatchCreatedEvent(
        UUID id,
        MatchId matchId,

        Integer durationSeconds,
        Long startDateTime,
        Long endDateTime,

        String lobbyType,
        String gameMode,

        Integer actualRank,
        Integer radiantKills,
        Integer direKills,

        String analysisOutcome,
        String bottomLaneOutcome,
        String midLaneOutcome,
        String topLaneOutcome,

        OffsetDateTime occurredAt

) implements DomainEvent {

    public MatchCreatedEvent(
            UUID id,
            MatchId matchId,
            Integer durationSeconds,
            Long startDateTime,
            Long endDateTime,
            String lobbyType,
            String gameMode,
            Integer actualRank,
            Integer radiantKills,
            Integer direKills,
            String analysisOutcome,
            String bottomLaneOutcome,
            String midLaneOutcome,
            String topLaneOutcome) {
        this(
                id,
                matchId,
                durationSeconds,
                startDateTime,
                endDateTime,
                lobbyType,
                gameMode,
                actualRank,
                radiantKills,
                direKills,
                analysisOutcome,
                bottomLaneOutcome,
                midLaneOutcome,
                topLaneOutcome,
                OffsetDateTime.now());
    }

    @Override
    public Object aggregateId() {
        return id;
    }

    @Override
    public String eventType() {
        return "match.created";
    }
}
