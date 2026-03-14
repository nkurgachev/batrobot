package com.batrobot.orchestration.application.dto.response;

import java.util.List;

/**
 * Aggregated player statistics for a selected period.
 */
public record StatsPeriodCommandResponse(
        Long periodStartEpoch,
        Long periodEndEpoch,
        List<AccountPeriodStats> accounts) {

    public record AccountPeriodStats(
            Long steamId64,
            String steamUsername,
            Integer totalMatches,
            Integer totalWins,
            Integer totalLosses,
            List<LobbyStats> lobbyStats,
            DurationStats durationStats,
            StreakStats streakStats,
            PerformanceStats performanceStats,
            List<PositionStats> positionStats,
            List<HeroStats> topHeroes,
            AchievementStats achievementStats) {
    }

    public record LobbyStats(
            String lobbyType,
            Integer matches,
            Integer wins,
            Integer losses,
            Integer winRatePercent) {
    }

    public record DurationStats(
            Integer averageDurationSeconds,
            Long fastestMatchId,
            Integer fastestDurationSeconds,
            Long longestMatchId,
            Integer longestDurationSeconds) {
    }

    public record StreakStats(
            Boolean currentIsVictory,
            Integer currentStreak,
            Integer maxWinStreak,
            Integer maxLossStreak) {
    }

    public record PerformanceStats(
            Integer averageImp,
            Double kdaRatio,
            Integer totalKills,
            Integer totalDeaths,
            Integer totalAssists,
            Integer averageGpm,
            Integer averageXpm,
            Double averageLastHits,
            Double averageDenies,
            Integer averageHeroDamage,
            Integer averageTowerDamage,
            Double averageObserverWardsPurchased,
            Double averageObserverWardsDestroyed,
            Double averageSentryWardsPurchased,
            Double averageSentryWardsDestroyed,
            Integer averageHeroHealing) {
    }

    public record PositionStats(
            String position,
            Integer matches,
            Integer gameWinRatePercent,
            Integer laneWinRatePercent) {
    }

    public record HeroStats(
            String heroName,
            Integer matches,
            Integer wins,
            Integer losses,
            Integer winRatePercent) {
    }

    public record AchievementStats(
            Integer mvpCount,
            Integer topCoreCount,
            Integer topSupportCount,
            Integer totalImp) {
    }
}