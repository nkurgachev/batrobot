package com.batrobot.stratz.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Intermediate DTO representing player matches data from Stratz API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StratzMatchesResponse {
    private Long steamId64;
    private List<StratzMatchResponse> matches;
}

