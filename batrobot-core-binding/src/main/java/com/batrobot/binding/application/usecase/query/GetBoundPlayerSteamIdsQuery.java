package com.batrobot.binding.application.usecase.query;

import com.batrobot.binding.domain.repository.ChatPlayerBindingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Query Use Case for getting Steam IDs of all players that have at least one binding.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GetBoundPlayerSteamIdsQuery {

    private final ChatPlayerBindingRepository bindingRepository;

    /**
     * Returns Steam ID 64-bit values for all players that have at least one chat binding.
     *
     * @return distinct list of Steam ID 64-bit values
     */
    @Transactional(readOnly = true)
    public List<Long> execute() {
        List<Long> steamIds = bindingRepository.findAllBoundSteamIds();
        log.debug("Found {} distinct bound Steam IDs", steamIds.size());
        return steamIds;
    }
}
