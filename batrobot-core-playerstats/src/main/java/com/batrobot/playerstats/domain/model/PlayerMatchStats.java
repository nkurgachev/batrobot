package com.batrobot.playerstats.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.batrobot.playerstats.domain.event.PlayerMatchStatsCreatedEvent;
import com.batrobot.shared.domain.model.BaseAggregateRoot;
import com.batrobot.shared.domain.model.valueobject.MatchId;
import com.batrobot.shared.domain.model.valueobject.SteamId;

/**
 * Domain Entity: PlayerMatchStats
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(of = { "id", "matchId", "steamId", "heroId", "heroName" })
public class PlayerMatchStats extends BaseAggregateRoot {

    // === Identity ===
    private final UUID id;
    private final MatchId matchId;
    private final SteamId steamId;

    // === PlayerMatchStats properties ===
    // Hero Info
    private Integer heroId;
    private String heroName;

    // Game Outcome
    private Boolean isVictory;
    private Boolean isRadiant;

    // Value Objects
    private PlayerKda kda;
    private PlayerEconomy economy;
    private PlayerCombat combat;

    // Positioning
    private String lane;
    private String position;

    // Performance
    private Integer imp;
    private String award;

    // Support Statistics
    private Integer campStack;
    private Integer courierKills;
    private Integer sentryWardsPurchased;
    private Integer observerWardsPurchased;
    private Integer sentryWardsDestroyed;
    private Integer observerWardsDestroyed;

    // === Audit fields ===
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    /**
     * Constructor for creating new PlayerMatchStats with all fields.
     */
    private PlayerMatchStats(
            MatchId matchId,
            SteamId steamId,
            Integer heroId,
            String heroName,
            Boolean isVictory,
            Boolean isRadiant,
            PlayerKda kda,
            PlayerEconomy economy,
            PlayerCombat combat,
            String lane,
            String position,
            Integer imp,
            String award,
            Integer campStack,
            Integer courierKills,
            Integer sentryWardsPurchased,
            Integer observerWardsPurchased,
            Integer sentryWardsDestroyed,
            Integer observerWardsDestroyed) {
        this.id = UUID.randomUUID();

        this.matchId = matchId;
        this.steamId = steamId;
        this.heroId = heroId;
        this.heroName = heroName;
        this.isVictory = isVictory;
        this.isRadiant = isRadiant;
        this.kda = kda;
        this.economy = economy;
        this.combat = combat;
        this.lane = lane;
        this.position = position;
        this.imp = imp;
        this.award = award;
        this.campStack = campStack;
        this.courierKills = courierKills;
        this.sentryWardsPurchased = sentryWardsPurchased;
        this.observerWardsPurchased = observerWardsPurchased;
        this.sentryWardsDestroyed = sentryWardsDestroyed;
        this.observerWardsDestroyed = observerWardsDestroyed;

        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * Constructor for reconstitution from persistence layer.
     */
    private PlayerMatchStats(
            UUID id,
            MatchId matchId,
            SteamId steamId,
            Integer heroId,
            String heroName,
            Boolean isVictory,
            Boolean isRadiant,
            PlayerKda kda,
            PlayerEconomy economy,
            PlayerCombat combat,
            String lane,
            String position,
            Integer imp,
            String award,
            Integer campStack,
            Integer courierKills,
            Integer sentryWardsPurchased,
            Integer observerWardsPurchased,
            Integer sentryWardsDestroyed,
            Integer observerWardsDestroyed,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt) {
        this.id = id;

        this.matchId = matchId;
        this.steamId = steamId;
        this.heroId = heroId;
        this.heroName = heroName;
        this.isVictory = isVictory;
        this.isRadiant = isRadiant;
        this.kda = kda;
        this.economy = economy;
        this.combat = combat;
        this.lane = lane;
        this.position = position;
        this.imp = imp;
        this.award = award;
        this.campStack = campStack;
        this.courierKills = courierKills;
        this.sentryWardsPurchased = sentryWardsPurchased;
        this.observerWardsPurchased = observerWardsPurchased;
        this.sentryWardsDestroyed = sentryWardsDestroyed;
        this.observerWardsDestroyed = observerWardsDestroyed;

        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ==================== Factory Methods ====================

    /**
     * Creates a new player match stats record with full data.
     */
    public static PlayerMatchStats create(
            MatchId matchId,
            SteamId steamId,
            Integer heroId,
            String heroName,
            Boolean isVictory,
            Boolean isRadiant,
            Integer kills,
            Integer deaths,
            Integer assists,
            Integer numLastHits,
            Integer numDenies,
            Integer goldPerMinute,
            Integer experiencePerMinute,
            Integer heroDamage,
            Integer towerDamage,
            Integer heroHealing,
            String lane,
            String position,
            Integer imp,
            String award,
            Integer campStack,
            Integer courierKills,
            Integer sentryWardsPurchased,
            Integer observerWardsPurchased,
            Integer sentryWardsDestroyed,
            Integer observerWardsDestroyed) {
        PlayerMatchStats stats = new PlayerMatchStats(
            matchId,
            steamId,
            heroId,
            heroName,
            isVictory,
            isRadiant,
            new PlayerKda(kills, deaths, assists),
            new PlayerEconomy(numLastHits, numDenies, goldPerMinute, experiencePerMinute),
            new PlayerCombat(heroDamage, towerDamage, heroHealing),
            lane,
            position,
            imp,
            award,
            campStack,
            courierKills,
            sentryWardsPurchased,
            observerWardsPurchased,
            sentryWardsDestroyed,
            observerWardsDestroyed
        );

        stats.registerEvent(new PlayerMatchStatsCreatedEvent(
            stats.matchId,
            stats.steamId,
            stats.heroName,
            stats.isVictory,
            stats.kda.kills(),
            stats.kda.deaths(),
            stats.kda.assists(),
            stats.position,
            stats.award,
            stats.imp));

        return stats;
    }

    /**
     * Reconstitutes from persistence layer.
     */
    public static PlayerMatchStats reconstitute(
            UUID id,
            MatchId matchId,
            SteamId steamId,
            Integer heroId,
            String heroName,
            Boolean isVictory,
            Boolean isRadiant,
            PlayerKda kda,
            PlayerEconomy economy,
            PlayerCombat combat,
            String lane,
            String position,
            Integer imp,
            String award,
            Integer campStack,
            Integer courierKills,
            Integer sentryWardsPurchased,
            Integer observerWardsPurchased,
            Integer sentryWardsDestroyed,
            Integer observerWardsDestroyed,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt) {
        return new PlayerMatchStats(
            id,
            matchId,
            steamId,
            heroId,
            heroName,
            isVictory,
            isRadiant,
            kda,
            economy,
            combat,
            lane,
            position,
            imp,
            award,
            campStack,
            courierKills,
            sentryWardsPurchased,
            observerWardsPurchased,
            sentryWardsDestroyed,
            observerWardsDestroyed,
            createdAt,
            updatedAt
        );
    }

    // ==================== Business Methods ====================

    /**
     * Updates hero info.
     */
    public boolean updateHero(Integer newHeroId, String newHeroName) {

        boolean changed = false;

        changed |= updateField(newHeroId, this.heroId, val -> this.heroId = val);
        changed |= updateField(newHeroName, this.heroName, val -> this.heroName = val);

        if (changed) {
            this.updatedAt = OffsetDateTime.now();
        }
        return changed;
    }

    /**
     * Updates game outcome.
     */
    public boolean updateGameOutcome(Boolean newIsVictory, Boolean newIsRadiant) {

        boolean changed = false;

        changed |= updateField(newIsVictory, this.isVictory, val -> this.isVictory = val);
        changed |= updateField(newIsRadiant, this.isRadiant, val -> this.isRadiant = val);

        if (changed) {
            this.updatedAt = OffsetDateTime.now();
        }
        return changed;
    }

    /**
     * Updates KDA statistics.
     */
    public boolean updateKda(Integer newKills, Integer newDeaths, Integer newAssists) {
        PlayerKda newKda = new PlayerKda(newKills, newDeaths, newAssists);

        boolean changed = false;

        changed |= updateField(newKda, this.kda, val -> this.kda = val);

        if (changed) {
            this.updatedAt = OffsetDateTime.now();
        }
        return changed;
    }

    /**
     * Updates economy metrics.
     */
    public boolean updateEconomy(Integer newNumLastHits, Integer newNumDenies,
            Integer newGoldPerMinute, Integer newExperiencePerMinute) {
        PlayerEconomy newEconomy = new PlayerEconomy(newNumLastHits, newNumDenies, newGoldPerMinute, newExperiencePerMinute);

        boolean changed = false;

        changed |= updateField(newEconomy, this.economy, val -> this.economy = val);

        if (changed) {
            this.updatedAt = OffsetDateTime.now();
        }
        return changed;
    }

    /**
     * Updates combat statistics.
     */
    public boolean updateCombat(Integer newHeroDamage, Integer newTowerDamage, Integer newHeroHealing) {
        PlayerCombat newCombat = new PlayerCombat(newHeroDamage, newTowerDamage, newHeroHealing);

        boolean changed = false;

        changed |= updateField(newCombat, this.combat, val -> this.combat = val);

        if (changed) {
            this.updatedAt = OffsetDateTime.now();
        }
        return changed;
    }

    /**
     * Updates positioning information.
     */
    public boolean updatePosition(String newLane, String newPosition) {

        boolean changed = false;

        changed |= updateField(newLane, this.lane, val -> this.lane = val);
        changed |= updateField(newPosition, this.position, val -> this.position = val);

        if (changed) {
            this.updatedAt = OffsetDateTime.now();
        }
        return changed;
    }

    /**
     * Updates perfomance.
     */
    public boolean updatePerfomance(Integer newImp, String newAward) {

        boolean changed = false;

        changed |= updateField(newImp, this.imp, val -> this.imp = val);
        changed |= updateField(newAward, this.award, val -> this.award = val);

        if (changed) {
            this.updatedAt = OffsetDateTime.now();
        }
        return changed;
    }

    /**
     * Updates support contributions.
     */
    public boolean updateSupportStats(Integer newCampStack, Integer newCourierKills,
            Integer newSentryWardsPurchased, Integer newObserverWardsPurchased,
            Integer newSentryWardsDestroyed, Integer newObserverWardsDestroyed) {

        boolean changed = false;

        changed |= updateField(newCampStack, this.campStack, val -> this.campStack = val);
        changed |= updateField(newCourierKills, this.courierKills, val -> this.courierKills = val);
        changed |= updateField(newSentryWardsPurchased, this.sentryWardsPurchased, val -> this.sentryWardsPurchased = val);
        changed |= updateField(newObserverWardsPurchased, this.observerWardsPurchased, val -> this.observerWardsPurchased = val);
        changed |= updateField(newSentryWardsDestroyed, this.sentryWardsDestroyed, val -> this.sentryWardsDestroyed = val);
        changed |= updateField(newObserverWardsDestroyed, this.observerWardsDestroyed, val -> this.observerWardsDestroyed = val);

        if (changed) {
            this.updatedAt = OffsetDateTime.now();
        }
        return changed;
    }

    // ==================== Query Methods ====================

    /**
     * Checks if player had a successful game.
     */
    public boolean wasSuccessful() {
        return isVictory != null && isVictory;
    }

    /**
     * Gets KDA ratio using value object's calculation.
     */
    public double getKdaRatio() {
        return kda != null ? kda.getKdaRatio() : 0.0;
    }

    /**
     * Gets creep score (CS) = Last Hits + Denies.
     */
    public int getCreepScore() {
        return economy != null ? economy.getCreepScore() : 0;
    }

    /**
     * Gets total damage dealt.
     */
    public long getTotalDamage() {
        return combat != null ? combat.getTotalDamage() : 0;
    }

    /**
     * Checks if player is on radiant team.
     */
    public boolean isRadiantTeam() {
        return isRadiant != null && isRadiant;
    }

    /**
     * Checks if player provided good support.
     */
    public boolean providedGoodSupport() {
        int wards = (sentryWardsPurchased != null ? sentryWardsPurchased : 0) +
                (observerWardsPurchased != null ? observerWardsPurchased : 0);
        return wards > 5;
    }
}

