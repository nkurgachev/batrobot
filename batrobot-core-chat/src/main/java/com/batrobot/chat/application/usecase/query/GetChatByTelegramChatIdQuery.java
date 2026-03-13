package com.batrobot.chat.application.usecase.query;

import com.batrobot.chat.application.dto.response.ChatResponse;
import com.batrobot.chat.application.mapper.ChatMapper;
import com.batrobot.chat.domain.repository.ChatRepository;
import com.batrobot.shared.domain.model.valueobject.TelegramChatId;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

/**
 * Query Use Case for getting a Telegram chat by external Telegram ID.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class GetChatByTelegramChatIdQuery {

    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;

    /**
     * Gets a Telegram chat by their Telegram ID (external ID).
     * 
     * @param telegramChatId Telegram chat ID value
     * @return Optional with chat response if found, empty otherwise
     */
    @Transactional(readOnly = true)
    public Optional<ChatResponse> execute(@NotNull Long telegramChatId) {
        log.debug("Getting Telegram chat by Telegram ID: {}", telegramChatId);

        return chatRepository.findByTelegramChatId(TelegramChatId.of(telegramChatId))
            .map(chat -> {
                log.debug("Found chat: telegramChatId={}", telegramChatId);
                return chatMapper.toResponse(chat);
            });
    }
}

