package com.batrobot.stratz.infrastructure.projection;

import io.github.nkurgachev.stratz.generated.client.PlayersProjectionRoot;

import lombok.experimental.UtilityClass;

/**
 * GraphQL projection builder for Stratz Players query.
 */
@UtilityClass
public class PlayersProjection {
    /**
     * Builds a complete projection for fetching Steam account information with all required fields.
     * 
     * @return Configured PlayersProjectionRoot with all necessary fields
     */
    public static PlayersProjectionRoot<?, ?> buildPlayersProjection() {
        return new PlayersProjectionRoot<>()
                // Steam account information
                .steamAccount()
                    .id()

                    // Profile information
                    .avatar()
                    .name()
                    .profileUri()
                    .timeCreated()

                    // Flags and visibility
                    .communityVisibleState()
                    .isAnonymous()
                    .isDotaPlusSubscriber()
                    .isStratzPublic()
                    .smurfFlag()

                    // Rank and activity
                    .seasonRank()
                    .activity().activity().parent().parent()
                    .parent()

                // Match statistics    
                .matchCount()
                .winCount()
                .firstMatchDate()
                .lastMatchDate()
                .imp();
    }
}

