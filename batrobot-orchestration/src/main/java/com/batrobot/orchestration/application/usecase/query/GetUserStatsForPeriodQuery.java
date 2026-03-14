package com.batrobot.orchestration.application.usecase.query;

import com.batrobot.binding.application.dto.response.ChatPlayerBindingResponse;
import com.batrobot.binding.application.usecase.query.GetBindingsForUserInChatQuery;
import com.batrobot.match.application.dto.response.MatchResponse;
import com.batrobot.match.application.usecase.query.GetRecentMatchesQuery;
import com.batrobot.orchestration.application.dto.request.CommonRequest;
import com.batrobot.orchestration.application.dto.response.StatsPeriodCommandResponse;
import com.batrobot.orchestration.application.dto.response.StatsPeriodCommandResponse.AccountPeriodStats;
import com.batrobot.orchestration.application.dto.response.StatsPeriodCommandResponse.AchievementStats;
import com.batrobot.orchestration.application.dto.response.StatsPeriodCommandResponse.DurationStats;
import com.batrobot.orchestration.application.dto.response.StatsPeriodCommandResponse.HeroStats;
import com.batrobot.orchestration.application.dto.response.StatsPeriodCommandResponse.LobbyStats;
import com.batrobot.orchestration.application.dto.response.StatsPeriodCommandResponse.PerformanceStats;
import com.batrobot.orchestration.application.dto.response.StatsPeriodCommandResponse.PositionStats;
import com.batrobot.orchestration.application.dto.response.StatsPeriodCommandResponse.StreakStats;
import com.batrobot.orchestration.application.exception.OrchestrationUserNoAccountsException;
import com.batrobot.orchestration.application.exception.OrchestrationUserNoMatchesForPeriodException;
import com.batrobot.orchestration.application.mapper.OrchestrationRequestMapper;
import com.batrobot.player.application.dto.response.PlayerResponse;
import com.batrobot.player.application.usecase.query.GetPlayersBySteamIdsQuery;
import com.batrobot.playerstats.application.dto.response.PlayerMatchStatsResponse;
import com.batrobot.playerstats.application.usecase.query.GetStatsForPlayersInMatchesQuery;
import com.batrobot.shared.application.port.config.AppDayTimeConfig;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Query use case that builds user stats for a selected period in a chat.
 * Period end is always current moment in configured timezone.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class GetUserStatsForPeriodQuery {

    private static final List<String> LOBBY_ORDER = List.of("RANKED", "UNRANKED", "BATTLE_CUP");
    private static final List<String> POSITION_ORDER = List.of("POSITION_1", "POSITION_2", "POSITION_3", "POSITION_4", "POSITION_5");

    private final GetBindingsForUserInChatQuery getBindingsForUserInChatQuery;
    private final GetRecentMatchesQuery getRecentMatchesQuery;
    private final GetStatsForPlayersInMatchesQuery getStatsForPlayersInMatchesQuery;
    private final GetPlayersBySteamIdsQuery getPlayersBySteamIdsQuery;

    private final OrchestrationRequestMapper requestMapper;
    private final AppDayTimeConfig dayTimeConfig;

    public StatsPeriodCommandResponse execute(@Valid CommonRequest request, @NotNull Long periodStartEpoch)
            throws OrchestrationUserNoAccountsException,
            OrchestrationUserNoMatchesForPeriodException {
        Long chatId = requestMapper.toTelegramChatId(request);
        Long userId = requestMapper.toTelegramUserId(request);

        ZoneId zoneId = ZoneId.of(dayTimeConfig.getTimezone());
        long periodEnd = OffsetDateTime.now(zoneId).toEpochSecond();
        long periodStart = Math.min(periodStartEpoch, periodEnd);

        log.debug("Fetching period stats for user {} in chat {}, period {}..{}", userId, chatId, periodStart,
                periodEnd);

        List<ChatPlayerBindingResponse> bindings = getBindingsForUserInChatQuery.execute(chatId, userId);
        if (bindings.isEmpty()) {
            throw new OrchestrationUserNoAccountsException(chatId, userId);
        }

        List<Long> steamIds = bindings.stream()
                .map(ChatPlayerBindingResponse::steamId64)
                .distinct()
                .toList();

        List<MatchResponse> recentMatches = getRecentMatchesQuery.execute(periodStart).stream()
                .filter(match -> match.startDateTime() != null && match.startDateTime() <= periodEnd)
                .toList();

        if (recentMatches.isEmpty()) {
            throw new OrchestrationUserNoMatchesForPeriodException(chatId, userId);
        }

        Map<Long, MatchResponse> matchesById = recentMatches.stream()
                .collect(Collectors.toMap(MatchResponse::matchId, Function.identity()));

        List<Long> matchIds = new ArrayList<>(matchesById.keySet());
        List<PlayerMatchStatsResponse> stats = getStatsForPlayersInMatchesQuery.execute(matchIds, steamIds).stream()
                .filter(s -> matchesById.containsKey(s.matchId()))
                .toList();

        if (stats.isEmpty()) {
            throw new OrchestrationUserNoMatchesForPeriodException(chatId, userId);
        }

        Map<Long, PlayerResponse> playerBySteamId = getPlayersBySteamIdsQuery.execute(steamIds).stream()
                .collect(Collectors.toMap(PlayerResponse::steamId64, Function.identity()));

        Map<Long, List<PlayerMatchStatsResponse>> statsBySteamId = stats.stream()
                .collect(Collectors.groupingBy(PlayerMatchStatsResponse::steamId64));

        List<AccountPeriodStats> accounts = bindings.stream()
                .map(ChatPlayerBindingResponse::steamId64)
                .distinct()
                .map(steamId -> buildAccountStats(
                        steamId,
                        statsBySteamId.getOrDefault(steamId, List.of()),
                        matchesById,
                        playerBySteamId.get(steamId)))
                .filter(Objects::nonNull)
                .toList();

        if (accounts.isEmpty()) {
            throw new OrchestrationUserNoMatchesForPeriodException(chatId, userId);
        }

        return new StatsPeriodCommandResponse(periodStart, periodEnd, accounts);
    }

    private AccountPeriodStats buildAccountStats(
            Long steamId,
            List<PlayerMatchStatsResponse> playerStats,
            Map<Long, MatchResponse> matchesById,
            PlayerResponse player) {

        if (playerStats.isEmpty()) {
            return null;
        }

        List<PlayerMatchStatsResponse> statsSortedByTime = playerStats.stream()
                .sorted(Comparator.comparing(s -> Optional.ofNullable(matchesById.get(s.matchId()))
                        .map(MatchResponse::startDateTime)
                        .orElse(0L)))
                .toList();

        int totalMatches = playerStats.size();
        int totalWins = (int) playerStats.stream().filter(s -> Boolean.TRUE.equals(s.isVictory())).count();
        int totalLosses = (int) playerStats.stream().filter(s -> Boolean.FALSE.equals(s.isVictory())).count();

        List<LobbyStats> lobbyStats = buildLobbyStats(playerStats, matchesById);
        DurationStats durationStats = buildDurationStats(playerStats, matchesById);
        StreakStats streakStats = buildStreakStats(statsSortedByTime);
        PerformanceStats performanceStats = buildPerformanceStats(playerStats);
        List<PositionStats> positionStats = buildPositionStats(playerStats, matchesById);
        List<HeroStats> topHeroes = buildTopHeroes(playerStats);
        AchievementStats achievementStats = buildAchievementStats(playerStats);

        String steamUsername = player != null && player.steamUsername() != null
                ? player.steamUsername()
                : "Steam " + steamId;

        return new AccountPeriodStats(
                steamId,
                steamUsername,
                totalMatches,
                totalWins,
                totalLosses,
                lobbyStats,
                durationStats,
                streakStats,
                performanceStats,
                positionStats,
                topHeroes,
                achievementStats);
    }

    private List<LobbyStats> buildLobbyStats(List<PlayerMatchStatsResponse> playerStats, Map<Long, MatchResponse> matchesById) {
        return LOBBY_ORDER.stream()
                .map(lobbyType -> {
                    List<PlayerMatchStatsResponse> lobbyMatches = playerStats.stream()
                            .filter(s -> {
                                MatchResponse match = matchesById.get(s.matchId());
                                return match != null && lobbyType.equals(match.lobbyType());
                            })
                            .toList();

                    if (lobbyMatches.isEmpty()) {
                        return null;
                    }

                    int matches = lobbyMatches.size();
                    int wins = (int) lobbyMatches.stream().filter(s -> Boolean.TRUE.equals(s.isVictory())).count();
                    int losses = (int) lobbyMatches.stream().filter(s -> Boolean.FALSE.equals(s.isVictory())).count();
                    int winRate = toPercent(wins, matches);

                    return new LobbyStats(lobbyType, matches, wins, losses, winRate);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private DurationStats buildDurationStats(List<PlayerMatchStatsResponse> playerStats, Map<Long, MatchResponse> matchesById) {
        List<MatchDuration> durations = playerStats.stream()
                .map(s -> {
                    MatchResponse match = matchesById.get(s.matchId());
                    if (match == null) {
                        return null;
                    }

                    Integer duration = resolveDurationSeconds(match);
                    if (duration == null || duration <= 0) {
                        return null;
                    }

                    return new MatchDuration(match.matchId(), duration);
                })
                .filter(Objects::nonNull)
                .toList();

        if (durations.isEmpty()) {
            return new DurationStats(0, null, null, null, null);
        }

        int averageDuration = (int) Math.round(durations.stream()
                .mapToInt(MatchDuration::durationSeconds)
                .average()
                .orElse(0.0));

        MatchDuration fastest = durations.stream()
                .min(Comparator.comparingInt(MatchDuration::durationSeconds))
                .orElseThrow();

        MatchDuration longest = durations.stream()
                .max(Comparator.comparingInt(MatchDuration::durationSeconds))
                .orElseThrow();

        return new DurationStats(
                averageDuration,
                fastest.matchId(),
                fastest.durationSeconds(),
                longest.matchId(),
                longest.durationSeconds());
    }

    private Integer resolveDurationSeconds(MatchResponse match) {
        if (match.startDateTime() != null && match.endDateTime() != null) {
            long calculated = match.endDateTime() - match.startDateTime();
            if (calculated > 0 && calculated <= Integer.MAX_VALUE) {
                return (int) calculated;
            }
        }
        return match.durationSeconds();
    }

    private StreakStats buildStreakStats(List<PlayerMatchStatsResponse> sortedStats) {
        List<Boolean> results = sortedStats.stream()
                .map(PlayerMatchStatsResponse::isVictory)
                .filter(Objects::nonNull)
                .toList();

        if (results.isEmpty()) {
            return new StreakStats(null, 0, 0, 0);
        }

        int maxWin = 0;
        int maxLoss = 0;
        int currentWin = 0;
        int currentLoss = 0;

        for (Boolean result : results) {
            if (Boolean.TRUE.equals(result)) {
                currentWin++;
                currentLoss = 0;
            } else {
                currentLoss++;
                currentWin = 0;
            }
            maxWin = Math.max(maxWin, currentWin);
            maxLoss = Math.max(maxLoss, currentLoss);
        }

        Boolean currentResult = results.getLast();
        int currentStreak = 0;
        for (int i = results.size() - 1; i >= 0; i--) {
            if (Objects.equals(results.get(i), currentResult)) {
                currentStreak++;
            } else {
                break;
            }
        }

        return new StreakStats(currentResult, currentStreak, maxWin, maxLoss);
    }

    private PerformanceStats buildPerformanceStats(List<PlayerMatchStatsResponse> playerStats) {
        int totalKills = playerStats.stream().map(PlayerMatchStatsResponse::kills).filter(Objects::nonNull)
                .mapToInt(Integer::intValue).sum();
        int totalDeaths = playerStats.stream().map(PlayerMatchStatsResponse::deaths).filter(Objects::nonNull)
                .mapToInt(Integer::intValue).sum();
        int totalAssists = playerStats.stream().map(PlayerMatchStatsResponse::assists).filter(Objects::nonNull)
                .mapToInt(Integer::intValue).sum();

        double kda = totalDeaths > 0
                ? (double) (totalKills + totalAssists) / totalDeaths
                : (double) (totalKills + totalAssists);

        return new PerformanceStats(
                averageInt(playerStats, PlayerMatchStatsResponse::imp),
                roundToOne(kda),
                totalKills,
                totalDeaths,
                totalAssists,
                averageInt(playerStats, PlayerMatchStatsResponse::goldPerMinute),
                averageInt(playerStats, PlayerMatchStatsResponse::experiencePerMinute),
                averageDouble(playerStats, PlayerMatchStatsResponse::numLastHits),
                averageDouble(playerStats, PlayerMatchStatsResponse::numDenies),
                averageInt(playerStats, PlayerMatchStatsResponse::heroDamage),
                averageInt(playerStats, PlayerMatchStatsResponse::towerDamage),
                averageDouble(playerStats, PlayerMatchStatsResponse::observerWardsPurchased),
                averageDouble(playerStats, PlayerMatchStatsResponse::observerWardsDestroyed),
                averageDouble(playerStats, PlayerMatchStatsResponse::sentryWardsPurchased),
                averageDouble(playerStats, PlayerMatchStatsResponse::sentryWardsDestroyed),
                averageInt(playerStats, PlayerMatchStatsResponse::heroHealing));
    }

    private List<PositionStats> buildPositionStats(
            List<PlayerMatchStatsResponse> playerStats,
            Map<Long, MatchResponse> matchesById) {
        return POSITION_ORDER.stream()
                .map(position -> {
                    List<PlayerMatchStatsResponse> positionMatches = playerStats.stream()
                            .filter(s -> position.equals(s.position()))
                            .toList();

                    if (positionMatches.isEmpty()) {
                        return null;
                    }

                    int matches = positionMatches.size();
                    int wins = (int) positionMatches.stream().filter(s -> Boolean.TRUE.equals(s.isVictory())).count();
                    int gameWinRate = toPercent(wins, matches);

                    List<Integer> laneResults = positionMatches.stream()
                            .map(s -> resolveLaneWinResult(s, matchesById.get(s.matchId())))
                            .filter(Objects::nonNull)
                            .toList();

                    int laneWins = laneResults.stream().mapToInt(Integer::intValue).sum();
                    int laneWinRate = laneResults.isEmpty() ? 0 : toPercent(laneWins, laneResults.size());

                    return new PositionStats(position, matches, gameWinRate, laneWinRate);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private Integer resolveLaneWinResult(PlayerMatchStatsResponse stats, MatchResponse match) {
        if (match == null || stats.isRadiant() == null || stats.lane() == null) {
            return null;
        }

        String laneOutcome = resolveLaneOutcome(stats.lane(), stats.isRadiant(), match);
        if (laneOutcome == null) {
            return null;
        }

        return switch (laneOutcome) {
            case "RADIANT_VICTORY", "RADIANT_STOMP" -> stats.isRadiant() ? 1 : 0;
            case "DIRE_VICTORY", "DIRE_STOMP" -> stats.isRadiant() ? 0 : 1;
            default -> null;
        };
    }

    private String resolveLaneOutcome(String lane, boolean isRadiant, MatchResponse match) {
        return switch (lane) {
            case "SAFE_LANE" -> isRadiant ? match.bottomLaneOutcome() : match.topLaneOutcome();
            case "MID_LANE" -> match.midLaneOutcome();
            case "OFF_LANE" -> isRadiant ? match.topLaneOutcome() : match.bottomLaneOutcome();
            default -> null;
        };
    }

    private List<HeroStats> buildTopHeroes(List<PlayerMatchStatsResponse> playerStats) {
        Map<String, List<PlayerMatchStatsResponse>> byHero = playerStats.stream()
                .filter(s -> s.heroName() != null && !s.heroName().isBlank())
                .collect(Collectors.groupingBy(PlayerMatchStatsResponse::heroName));

        return byHero.entrySet().stream()
                .map(entry -> {
                    String heroName = entry.getKey();
                    List<PlayerMatchStatsResponse> heroMatches = entry.getValue();
                    int matches = heroMatches.size();
                    int wins = (int) heroMatches.stream().filter(s -> Boolean.TRUE.equals(s.isVictory())).count();
                    int losses = (int) heroMatches.stream().filter(s -> Boolean.FALSE.equals(s.isVictory())).count();
                    int winRate = toPercent(wins, matches);
                    return new HeroStats(heroName, matches, wins, losses, winRate);
                })
                .sorted(Comparator
                        .comparingInt(HeroStats::matches).reversed()
                        .thenComparing(Comparator.comparingInt(HeroStats::wins).reversed())
                        .thenComparing(HeroStats::heroName, String.CASE_INSENSITIVE_ORDER))
                .limit(3)
                .toList();
    }

    private AchievementStats buildAchievementStats(List<PlayerMatchStatsResponse> playerStats) {
        int mvp = (int) playerStats.stream().filter(s -> "MVP".equals(s.award())).count();
        int topCore = (int) playerStats.stream().filter(s -> "TOP_CORE".equals(s.award())).count();
        int topSupport = (int) playerStats.stream().filter(s -> "TOP_SUPPORT".equals(s.award())).count();
        int impSum = playerStats.stream()
                .map(PlayerMatchStatsResponse::imp)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        return new AchievementStats(mvp, topCore, topSupport, impSum);
    }

    private int averageInt(List<PlayerMatchStatsResponse> stats, Function<PlayerMatchStatsResponse, Integer> extractor) {
        return (int) Math.round(stats.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0));
    }

    private double averageDouble(List<PlayerMatchStatsResponse> stats, Function<PlayerMatchStatsResponse, Integer> extractor) {
        double average = stats.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .mapToDouble(Integer::doubleValue)
                .average()
                .orElse(0.0);
        return roundToOne(average);
    }

    private int toPercent(int value, int total) {
        if (total <= 0) {
            return 0;
        }
        return (int) Math.round((value * 100.0) / total);
    }

    private double roundToOne(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private record MatchDuration(Long matchId, Integer durationSeconds) {
    }
}