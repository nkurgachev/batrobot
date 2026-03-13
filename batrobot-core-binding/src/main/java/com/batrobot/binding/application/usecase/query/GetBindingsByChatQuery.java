package com.batrobot.binding.application.usecase.query;

import com.batrobot.binding.application.dto.response.ChatPlayerBindingResponse;
import com.batrobot.binding.application.mapper.ChatPlayerBindingMapper;
import com.batrobot.binding.domain.model.ChatPlayerBinding;
import com.batrobot.binding.domain.repository.ChatPlayerBindingRepository;
import com.batrobot.shared.domain.model.valueobject.TelegramChatId;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Query Use Case for fetching Steam bindings from chat.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class GetBindingsByChatQuery {

    private final ChatPlayerBindingRepository bindingRepository;
    private final ChatPlayerBindingMapper bindingMapper;

    /**
     * Fetches all bindings in a specific chat.
     * 
     * @param telegramChatId Telegram chat ID
     * @return List of ChatPlayerBindingResponse
     */
    @Transactional(readOnly = true)
    public List<ChatPlayerBindingResponse> execute(@NotNull Long telegramChatId) {
        log.debug("Fetching bindings for chat {}", telegramChatId);

        List<ChatPlayerBinding> bindings = bindingRepository.findBindingsInChat(TelegramChatId.of(telegramChatId));

        List<ChatPlayerBindingResponse> responses = bindings.stream()
                .map(bindingMapper::toResponse)
                .toList();

        log.debug("Found {} bindings for chat {}", responses.size(), telegramChatId);
        return responses;
    }
}

