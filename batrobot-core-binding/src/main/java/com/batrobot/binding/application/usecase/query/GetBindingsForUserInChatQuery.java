package com.batrobot.binding.application.usecase.query;

import com.batrobot.binding.application.dto.response.ChatPlayerBindingResponse;
import com.batrobot.binding.application.mapper.ChatPlayerBindingMapper;
import com.batrobot.binding.domain.model.ChatPlayerBinding;
import com.batrobot.binding.domain.repository.ChatPlayerBindingRepository;
import com.batrobot.shared.domain.model.valueobject.TelegramChatId;
import com.batrobot.shared.domain.model.valueobject.TelegramUserId;

import jakarta.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class GetBindingsForUserInChatQuery {
    
    private final ChatPlayerBindingRepository bindingRepository;
    private final ChatPlayerBindingMapper bindingMapper;

    /**
     * Fetches all bindings in a specific chat and user.
     * 
     * @param telegramChatId Telegram chat ID
     * @param telegramUserId Telegram user ID
     * @return List of ChatPlayerBindingResponse
     */
    @Transactional(readOnly = true)
    public List<ChatPlayerBindingResponse> execute(@NotNull Long telegramChatId, @NotNull Long telegramUserId) {
        log.debug("Fetching bindings for chat {} and user {}", telegramChatId, telegramUserId);

        List<ChatPlayerBinding> bindings = bindingRepository.findBindingsForUserInChat(TelegramChatId.of(telegramChatId), TelegramUserId.of(telegramUserId));

        List<ChatPlayerBindingResponse> responses = bindings.stream()
                .map(bindingMapper::toResponse)
                .toList();

        log.debug("Found {} bindings for chat {} and user {}", responses.size(), telegramChatId, telegramUserId);
        return responses;
    }
}

