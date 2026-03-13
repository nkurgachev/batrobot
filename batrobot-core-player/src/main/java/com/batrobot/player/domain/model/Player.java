package com.batrobot.player.domain.model;

import com.batrobot.player.domain.event.PlayerCreatedEvent;
import com.batrobot.player.domain.event.PlayerRankUpdatedEvent;
import com.batrobot.player.domain.event.PlayerUsernameUpdatedEvent;
import com.batrobot.shared.domain.model.BaseAggregateRoot;
import com.batrobot.shared.domain.model.valueobject.SeasonRank;
import com.batrobot.shared.domain.model.valueobject.SteamId;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Domain Entity representing a Steam/Dota2 player account.
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(of = { "id", "steamId", "steamUsername", "seasonRank" })
public class Player extends BaseAggregateRoot {

    // === Identity ===
    private final UUID id;
    private final SteamId steamId;

    // === Player properties ===
    // Profile information
    private String steamUsername;
    private String profileUrl;
    private String avatarUrl;
    private Long accountCreationDate;

    // Visibility and privacy settings
    private Integer communityVisibleState;
    private Boolean isAnonymous;
    private Boolean isStratzPublic;

    // Flags
    private Boolean isDotaPlusSubscriber;
    private Integer smurfFlag;

    // Rank and activity
    private SeasonRank seasonRank;
    private String activity;
    private Integer imp;

    // Match statistics
    private Integer matchCount;
    private Integer winCount;

    // Match date range
    private Long firstMatchDate;
    private Long lastMatchDate;

    // === Audit fields ===
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    /**
     * Constructor for creating new Player with all fields.
     */
    private Player(
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
        this.id = UUID.randomUUID();

        this.steamId = steamId;
        this.steamUsername = steamUsername;
        this.profileUrl = profileUrl;
        this.avatarUrl = avatarUrl;
        this.accountCreationDate = accountCreationDate;

        this.communityVisibleState = communityVisibleState;
        this.isAnonymous = isAnonymous;
        this.isStratzPublic = isStratzPublic;

        this.isDotaPlusSubscriber = isDotaPlusSubscriber;
        this.smurfFlag = smurfFlag;

        this.seasonRank = seasonRank;
        this.activity = activity;
        this.imp = imp;

        this.matchCount = matchCount;
        this.winCount = winCount;
        this.firstMatchDate = firstMatchDate;
        this.lastMatchDate = lastMatchDate;

        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * Constructor for reconstitution from persistence layer.
     */
    private Player(
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
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt) {
        this.id = id;

        this.steamId = steamId;
        this.steamUsername = steamUsername;
        this.profileUrl = profileUrl;
        this.avatarUrl = avatarUrl;
        this.accountCreationDate = accountCreationDate;

        this.communityVisibleState = communityVisibleState;
        this.isAnonymous = isAnonymous;
        this.isStratzPublic = isStratzPublic;

        this.isDotaPlusSubscriber = isDotaPlusSubscriber;
        this.smurfFlag = smurfFlag;

        this.seasonRank = seasonRank;
        this.activity = activity;
        this.imp = imp;

        this.matchCount = matchCount;
        this.winCount = winCount;
        this.firstMatchDate = firstMatchDate;
        this.lastMatchDate = lastMatchDate;

        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ==================== Factory Methods ====================

    /**
     * Creates a new Player.
     */
    public static Player create(
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
        Player player = new Player(
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
                lastMatchDate);

        player.registerEvent(new PlayerCreatedEvent(
                player.id,
                player.steamId,
                player.steamUsername,
                player.profileUrl,
                player.avatarUrl,
                player.accountCreationDate,
                player.communityVisibleState,
                player.isAnonymous,
                player.isStratzPublic,
                player.isDotaPlusSubscriber,
                player.smurfFlag,
                player.seasonRank,
                player.activity,
                player.imp,
                player.matchCount,
                player.winCount,
                player.firstMatchDate,
                player.lastMatchDate));

        return player;
    }

    /**
     * Reconstitutes Player from persistence layer.
     */
    public static Player reconstitute(
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
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt) {
        return new Player(
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
                createdAt,
                updatedAt);
    }

    // ==================== Business Methods ====================

    /**
     * Updates profile information (URLs, etc).
     */
    public boolean updateProfile(
            String newSteamUsername,
            String newProfileUrl,
            String newAvatarUrl,
            Long newAccountCreationDate) {

        boolean changed = false;

        changed |= updateFieldWithEvent(newSteamUsername, this.steamUsername, val -> this.steamUsername = val,
            () -> new PlayerUsernameUpdatedEvent(this.id, this.steamId, newSteamUsername, this.steamUsername));
        changed |= updateField(newProfileUrl, this.profileUrl, val -> this.profileUrl = val);
        changed |= updateField(newAvatarUrl, this.avatarUrl, val -> this.avatarUrl = val);
        changed |= updateField(newAccountCreationDate, this.accountCreationDate, val -> this.accountCreationDate = val);

        if (changed) {
            this.updatedAt = OffsetDateTime.now();
        }
        return changed;
    }

    /**
     * Updates privacy and visibility settings.
     */
    public boolean updateVisibilitySettings(
            Integer newCommunityVisibleState,
            Boolean newIsAnonymous,
            Boolean newIsStratzPublic) {

        boolean changed = false;

        changed |= updateField(newCommunityVisibleState, this.communityVisibleState, val -> this.communityVisibleState = val);
        changed |= updateField(newIsAnonymous, this.isAnonymous, val -> this.isAnonymous = val);
        changed |= updateField(newIsStratzPublic, this.isStratzPublic, val -> this.isStratzPublic = val);

        if (changed) {
            this.updatedAt = OffsetDateTime.now();
        }

        return changed;
    }

    /**
     * Updates Dota Plus subscription status.
     */
    public boolean updateIsDotaPlusSubscriber(boolean newIsDotaPlusSubscriber) {

        boolean changed = false;

        changed |= updateField(newIsDotaPlusSubscriber, this.isDotaPlusSubscriber, val -> this.isDotaPlusSubscriber = val);

        if (changed) {
            this.updatedAt = OffsetDateTime.now();
        }

        return changed;
    }

    /**
     * Updates smurf flag indicator.
     */
    public boolean updateSmurfFlag(Integer newSmurfFlag) {
        boolean changed = false;

        changed |= updateField(newSmurfFlag, this.smurfFlag, val -> this.smurfFlag = val);

        if (changed) {
            this.updatedAt = OffsetDateTime.now();
        }

        return changed;
    }

    /**
     * Updates rank information and activity from Stratz/OpenDota.
     * Activity is bundled with rank since both come from same Stratz API response.
     */
    public boolean updateRankInfo(
            SeasonRank newSeasonRank,
            Integer newImp,
            String newActivity) {

        boolean changed = false;

        changed |= updateFieldWithEvent(newSeasonRank, this.seasonRank, val -> this.seasonRank = val,
            () -> new PlayerRankUpdatedEvent(this.id, this.steamId, this.steamUsername, newSeasonRank, this.seasonRank));
        changed |= updateField(newImp, this.imp, val -> this.imp = val);
        changed |= updateField(newActivity, this.activity, val -> this.activity = val);

        if (changed) {
            this.updatedAt = OffsetDateTime.now();
        }

        return changed;
    }

    /**
     * Updates match statistics.
     */
    public boolean updateMatchStatistics(
            Integer newMatchCount,
            Integer newWinCount,
            Long newFirstMatchDate,
            Long newLastMatchDate) {

        boolean changed = false;

        changed |= updateField(newMatchCount, this.matchCount, val -> this.matchCount = val);
        changed |= updateField(newWinCount, this.winCount, val -> this.winCount = val);
        changed |= updateField(newFirstMatchDate, this.firstMatchDate, val -> this.firstMatchDate = val);
        changed |= updateField(newLastMatchDate, this.lastMatchDate, val -> this.lastMatchDate = val);

        if (changed) {
            this.updatedAt = OffsetDateTime.now();
        }

        return changed;
    }

    // ==================== Query Methods ====================

    public BigDecimal getWinRate() {
        if (matchCount == null || matchCount == 0) {
            return null;
        }
        if (winCount == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(winCount)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(matchCount), 2, RoundingMode.HALF_UP);
    }

    public Integer getLossCount() {
        if (matchCount == null || winCount == null) {
            return null;
        }
        return matchCount - winCount;
    }

    public boolean hasPublicProfile() {
        return communityVisibleState != null && communityVisibleState == 3;
    }

    public boolean isProfilePrivate() {
        return !hasPublicProfile();
    }

    public boolean isRankedPlayer() {
        return seasonRank != null && seasonRank.isRanked();
    }

    public boolean hasMatchHistory() {
        return matchCount != null && matchCount > 0;
    }

    public boolean isPotentialSmurf() {
        return smurfFlag != null && smurfFlag > 0;
    }
}

