package com.batrobot.chat.application.usecase.command;

import com.batrobot.chat.application.dto.request.ChatRequest;
import com.batrobot.chat.application.dto.response.ChatResponse;
import com.batrobot.chat.application.mapper.ChatMapper;
import com.batrobot.chat.domain.exception.ChatAlreadyExistsException;
import com.batrobot.chat.domain.model.Chat;
import com.batrobot.chat.domain.repository.ChatRepository;
import com.batrobot.chat.domain.specification.UniqueChatSpecification;
import com.batrobot.chat.domain.specification.UniqueChatSpecification.ChatContext;
import com.batrobot.shared.domain.model.valueobject.TelegramChatId;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * UseCase for creating a new Telegram chat.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class CreateChat {

    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;
    private final UniqueChatSpecification uniqueChatSpecification;

    /**
     * Creates a new Telegram chat.
     * 
     * @param request Chat data from Telegram
     * @return Created chat response
     * @throws ChatAlreadyExistsException if chat with this Telegram ID already exists
     */
    @Transactional
    public ChatResponse execute(@Valid ChatRequest request)
            throws ChatAlreadyExistsException {
        TelegramChatId telegramChatId = TelegramChatId.of(request.getTelegramChatId());

        log.debug("Creating new Telegram chat: telegramChatId={}", telegramChatId);

        uniqueChatSpecification.check(ChatContext.builder()
                .chatId(telegramChatId)
                .build());

        Chat newChat = chatMapper.createFromRequest(request);
        Chat savedChat = chatRepository.save(newChat);

        log.info("Successfully created new Telegram chat: {}", telegramChatId.value());

        return chatMapper.toResponse(savedChat);
    }
}

