package com.batrobot.chat.application.usecase.command;

import com.batrobot.chat.application.dto.request.ChatRequest;
import com.batrobot.chat.application.dto.response.ChatResponse;
import com.batrobot.chat.application.mapper.ChatMapper;
import com.batrobot.chat.domain.repository.ChatRepository;
import com.batrobot.shared.domain.model.valueobject.TelegramChatId;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * UseCase for upserting a Telegram chat.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class UpsertChat {

    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;

    /**
     * Upserts a Telegram chat - creates if not exists, updates if info changed.
     * Single database operation using repository.
     * 
     * @param request Chat request with Telegram data
     * @return Chat response
     */
    @Transactional
    public ChatResponse execute(@Valid ChatRequest request) {
        log.debug("Upserting Telegram chat: telegramChatId={}", request.getTelegramChatId());

        TelegramChatId telegramChatId = TelegramChatId.of(request.getTelegramChatId());

        return chatRepository.findByTelegramChatId(telegramChatId)
            .map(existing -> {
                // Chat exists, check if info needs updating
                boolean titleChanged = existing.updateTitle(request.getTitle());
                if (titleChanged) {
                    chatRepository.save(existing);
                    log.debug("Updated chat info for chat {}", telegramChatId.value());
                }
                return chatMapper.toResponse(existing);
            })
            .orElseGet(() -> {
                // Chat doesn't exist, create new one
                log.debug("Creating new chat: telegramChatId={}", telegramChatId.value());
                var newChat = chatMapper.createFromRequest(request);
                var savedChat = chatRepository.save(newChat);
                return chatMapper.toResponse(savedChat);
            });
    }
}

