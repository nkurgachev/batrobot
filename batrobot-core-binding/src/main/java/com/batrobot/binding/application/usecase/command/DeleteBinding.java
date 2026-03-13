package com.batrobot.binding.application.usecase.command;

import com.batrobot.binding.application.dto.request.DeleteBindingRequest;
import com.batrobot.binding.application.dto.response.ChatPlayerBindingResponse;
import com.batrobot.binding.application.exception.PlayerBindingNotFoundException;
import com.batrobot.binding.application.mapper.ChatPlayerBindingMapper;
import com.batrobot.binding.domain.model.ChatPlayerBinding;
import com.batrobot.binding.domain.repository.ChatPlayerBindingRepository;
import com.batrobot.shared.domain.model.valueobject.SteamId;
import com.batrobot.shared.domain.model.valueobject.TelegramChatId;
import com.batrobot.shared.domain.model.valueobject.TelegramUserId;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * UseCase for deleting a chat-player binding.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class DeleteBinding {

    private final ChatPlayerBindingRepository bindingRepository;
    private final ChatPlayerBindingMapper bindingMapper;

    /**
     * Deletes a binding between chat, user, and player.
     * 
     * @param request DeleteBindingRequest with chatId, telegramUserId, steamId64
     * @return Deleted binding response
     * @throws PlayerBindingNotFoundException if binding not found
     */
    @Transactional
    public ChatPlayerBindingResponse execute(@Valid DeleteBindingRequest request)
            throws PlayerBindingNotFoundException {

        TelegramChatId telegramChatId = TelegramChatId.of(request.getChatId());
        TelegramUserId telegramUserId = TelegramUserId.of(request.getUserId());
        SteamId steamId = SteamId.fromSteamId64(request.getSteamId64());

        log.debug("Deleting binding: steamId={}, userId={}, chatId={}", steamId, telegramUserId, telegramChatId);

        // Find binding
        ChatPlayerBinding binding = bindingRepository.findBindingForUser(telegramChatId, telegramUserId, steamId)
                .orElseThrow(() -> {
                    log.warn("Attempted to delete non-existent binding: steamId={}, userId={}, chatId={}",
                            steamId, telegramUserId, telegramChatId);
                    return new PlayerBindingNotFoundException(steamId, telegramUserId);
                });

        // Delete binding
        bindingRepository.deleteById(binding.getId());

        log.info("Successfully deleted binding: id={}, steamId={}, userId={}, chatId={}",
                binding.getId(), steamId, telegramUserId, telegramChatId);

        return bindingMapper.toResponse(binding);
    }
}

