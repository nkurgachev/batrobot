package com.batrobot.player.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Player data received from external sources.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerRequest {
    @NotNull(message = "Steam ID cannot be null")
    private Long steamId64;

    // Profile information
    private String avatarUrl;
    @NotNull(message = "Steam username cannot be null")
    private String steamUsername;
    private String profileUrl;
    private Long accountCreationDate;
    
    // Visibility and privacy settings
    private Integer communityVisibleState;
    private Boolean isAnonymous;
    private Boolean isStratzPublic;

    // Flags
    private Boolean isDotaPlusSubscriber;
    private Integer smurfFlag;

    // Rank and activity
    private Integer seasonRank;
    private String activity;
    private Integer imp;

    // Match statistics
    private Integer matchCount;
    private Integer winCount;
    private Long firstMatchDate;
    private Long lastMatchDate;
}

