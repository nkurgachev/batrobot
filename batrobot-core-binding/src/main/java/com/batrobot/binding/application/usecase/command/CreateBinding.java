package com.batrobot.binding.application.usecase.command;

import com.batrobot.binding.application.dto.request.ChatPlayerBindingRequest;
import com.batrobot.binding.application.dto.response.ChatPlayerBindingResponse;
import com.batrobot.binding.application.mapper.ChatPlayerBindingMapper;
import com.batrobot.binding.domain.exception.PlayerAlreadyBoundException;
import com.batrobot.binding.domain.model.ChatPlayerBinding;
import com.batrobot.binding.domain.repository.ChatPlayerBindingRepository;
import com.batrobot.binding.domain.specification.UniquePlayerBindingSpecification;
import com.batrobot.binding.domain.specification.UniquePlayerBindingSpecification.BindingContext;
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
 * UseCase for creating a new chat-player binding.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class CreateBinding {

    private final ChatPlayerBindingRepository bindingRepository;
    private final ChatPlayerBindingMapper bindingMapper;
    private final UniquePlayerBindingSpecification uniquePlayerBindingSpecification;

    /**
     * Creates a new binding between chat, user, and player.
     * 
     * @param request Binding request
     * @return Created binding response
     * @throws PlayerAlreadyBoundException if binding already exists
     */
    @Transactional
    public ChatPlayerBindingResponse execute(@Valid ChatPlayerBindingRequest request)
            throws PlayerAlreadyBoundException {

        TelegramChatId telegramChatId = TelegramChatId.of(request.getTelegramChatId());
        TelegramUserId telegramUserId = TelegramUserId.of(request.getTelegramUserId());
        SteamId steamId = SteamId.fromSteamId64(request.getSteamId64());

        log.debug("Creating new binding: steamId={}, userId={}, chatId={}", steamId, telegramUserId, telegramChatId);

        uniquePlayerBindingSpecification.check(BindingContext.builder()
                .telegramChatId(telegramChatId)
                .telegramUserId(telegramUserId)
                .steamId(steamId)
                .build());

        ChatPlayerBinding newBinding = bindingMapper.createFromRequest(request);
        ChatPlayerBinding saved = bindingRepository.save(newBinding);

        log.info("Successfully created binding: id={}, steamId={}, userId={}, chatId={}",
                saved.getId(), saved.getSteamId(), saved.getUserId(), saved.getChatId());

        return bindingMapper.toResponse(saved);
    }
}

