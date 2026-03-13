package com.batrobot.playerstats.application.usecase.command;

import com.batrobot.playerstats.application.dto.request.PlayerMatchStatsRequest;
import com.batrobot.playerstats.application.dto.response.PlayerMatchStatsResponse;
import com.batrobot.playerstats.application.mapper.PlayerMatchStatsMapper;
import com.batrobot.playerstats.domain.repository.PlayerMatchStatsRepository;
import com.batrobot.shared.domain.model.valueobject.MatchId;
import com.batrobot.shared.domain.model.valueobject.SteamId;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * Command Use Case for upserting PlayerMatchStats entity.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class UpsertPlayerMatchStats {

    private final PlayerMatchStatsRepository playerMatchStatsRepository;
    private final PlayerMatchStatsMapper playerMatchStatsMapper;

    /**
     * Upserts PlayerMatchStats — creates if not exists, updates if info changed.
     *
     * @param request PlayerMatchStatsRequest with stats data
     * @return PlayerMatchStatsResponse
     */
    @Transactional
    public PlayerMatchStatsResponse execute(@Valid PlayerMatchStatsRequest request) {
        MatchId matchId = MatchId.of(request.getMatchId());
        SteamId steamId = SteamId.fromSteamId64(request.getSteamId64());

        log.debug("Upserting player match stats: matchId={}, steamId={}",
                matchId.value(), steamId.value());

        return playerMatchStatsRepository.findByMatchIdAndSteamId(matchId, steamId)
                .map(existing -> {
                    boolean hasChanges = false;

                    hasChanges |= existing.updateHero(
                            request.getHeroId(),
                            request.getHeroName());

                    hasChanges |= existing.updateGameOutcome(
                            request.getIsVictory(),
                            request.getIsRadiant());

                    hasChanges |= existing.updateKda(
                            request.getKills(),
                            request.getDeaths(),
                            request.getAssists());

                    hasChanges |= existing.updateEconomy(
                            request.getNumLastHits(),
                            request.getNumDenies(),
                            request.getGoldPerMinute(),
                            request.getExperiencePerMinute());

                    hasChanges |= existing.updateCombat(
                            request.getHeroDamage(),
                            request.getTowerDamage(),
                            request.getHeroHealing());

                    hasChanges |= existing.updatePosition(
                            request.getLane(),
                            request.getPosition());

                    hasChanges |= existing.updatePerfomance(
                            request.getImp(),
                            request.getAward());

                    hasChanges |= existing.updateSupportStats(
                            request.getCampStack(),
                            request.getCourierKills(),
                            request.getSentryWardsPurchased(),
                            request.getObserverWardsPurchased(),
                            request.getSentryWardsDestroyed(),
                            request.getObserverWardsDestroyed());

                    if (hasChanges) {
                        playerMatchStatsRepository.save(existing);
                        log.debug("Updated player match stats: matchId={}, steamId={}",
                                matchId.value(), steamId.value());
                    }
                    return playerMatchStatsMapper.toResponse(existing);
                })
                .orElseGet(() -> {
                    log.debug("Creating new player match stats: matchId={}, steamId={}",
                            matchId.value(), steamId.value());

                    var newStats = playerMatchStatsMapper.createFromRequest(request);
                    var savedStats = playerMatchStatsRepository.save(newStats);

                    return playerMatchStatsMapper.toResponse(savedStats);
                });
    }
}

