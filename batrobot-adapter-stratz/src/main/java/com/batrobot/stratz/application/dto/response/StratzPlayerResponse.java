package com.batrobot.stratz.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * External DTO for Player data from Stratz GraphQL API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StratzPlayerResponse {
    private Long steamId64;

    // Profile information
    private String avatarUrl;
    private String steamUsername;
    private String profileUrl;
    private Long timeCreated;
    
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

