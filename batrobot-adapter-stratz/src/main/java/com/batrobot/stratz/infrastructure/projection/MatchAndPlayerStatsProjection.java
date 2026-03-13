package com.batrobot.stratz.infrastructure.projection;

import io.github.nkurgachev.stratz.generated.client.PlayerProjectionRoot;
import io.github.nkurgachev.stratz.generated.types.PlayerMatchesRequestType;

import lombok.experimental.UtilityClass;

/**
 * GraphQL projection builder for player matches query.
 */
@UtilityClass
public class MatchAndPlayerStatsProjection {

    public static PlayerProjectionRoot<?, ?> buildPlayerMatchesProjection(
            Long steamAccountId, 
            long startTime, 
            int limit
    ) {
        return new PlayerProjectionRoot<>()               
                .matches(PlayerMatchesRequestType.newBuilder()
                        .startDateTime(startTime)
                        .take(limit)
                        .build())
                    // Match-level fields
                    .id()
                    .durationSeconds()
                    .startDateTime()
                    .endDateTime()
                    .lobbyType().parent()
                    .gameMode().parent()
                    .actualRank()
                    .radiantKills()
                    .direKills()
                    .analysisOutcome().parent()
                    .bottomLaneOutcome().parent()
                    .midLaneOutcome().parent()
                    .topLaneOutcome().parent()

                    // Player stats
                    .players(steamAccountId)
                        .steamAccountId()
                        .isRadiant()
                        .isVictory()
                        .hero()
                            .id()
                            .displayName()
                            .parent()
                        .kills()
                        .deaths()
                        .assists()
                        .numLastHits()
                        .numDenies()
                        .goldPerMinute()
                        .experiencePerMinute()
                        .heroDamage()
                        .towerDamage()
                        .heroHealing()
                        .lane().parent()
                        .position().parent()
                        .imp()
                        .award().parent()
                        .stats()
                            .courierKills()
                                .time()
                                .parent()
                            .wards()
                                .time()
                                .type()
                                .parent()
                            .campStack()
                            .wardDestruction()
                                .time()
                                .gold()
                                .isWard()
                                .parent()
                            .parent()
                        .parent()
                    .parent();
    }
}

