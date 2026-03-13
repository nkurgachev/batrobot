package com.batrobot.binding.application.usecase.query;

import com.batrobot.binding.application.dto.response.ChatPlayerBindingResponse;
import com.batrobot.binding.application.mapper.ChatPlayerBindingMapper;
import com.batrobot.binding.domain.model.ChatPlayerBinding;
import com.batrobot.binding.domain.repository.ChatPlayerBindingRepository;
import com.batrobot.shared.domain.model.valueobject.SteamId;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Query Use Case for fetching all bindings associated with a Steam account.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class GetBindingsForSteamAccountQuery {

    private final ChatPlayerBindingRepository bindingRepository;
    private final ChatPlayerBindingMapper bindingMapper;

    /**
     * Fetches all bindings for a specific Steam account across all chats.
     *
     * @param steamId64 64-bit Steam ID
     * @return List of ChatPlayerBindingResponse
     */
    @Transactional(readOnly = true)
    public List<ChatPlayerBindingResponse> execute(@NotNull Long steamId64) {
        log.debug("Fetching bindings for Steam account {}", steamId64);

        List<ChatPlayerBinding> bindings = bindingRepository.findBindingsForSteamAccount(
                SteamId.fromSteamId64(steamId64));

        List<ChatPlayerBindingResponse> responses = bindings.stream()
                .map(bindingMapper::toResponse)
                .toList();

        log.debug("Found {} bindings for Steam account {}", responses.size(), steamId64);
        return responses;
    }
}
