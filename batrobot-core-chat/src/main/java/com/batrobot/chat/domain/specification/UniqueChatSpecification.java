package com.batrobot.chat.domain.specification;

import com.batrobot.chat.domain.exception.ChatAlreadyExistsException;
import com.batrobot.chat.domain.repository.ChatRepository;
import com.batrobot.shared.domain.model.valueobject.TelegramChatId;
import com.batrobot.shared.domain.specification.Specification;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * Specification for ensuring that a chat (identified by Telegram chat ID) is not already existing.
 */
@RequiredArgsConstructor
public class UniqueChatSpecification implements
        Specification<UniqueChatSpecification.ChatContext> {

    private final ChatRepository chatRepository;

    /**
     * Context for checking the specification.
     * Contains all necessary data for checking uniqueness.
     */
    @Value
    @Builder
    public static class ChatContext {
        TelegramChatId chatId;
    }

    @Override
    public boolean isSatisfiedBy(ChatContext context) {
        return chatRepository
                .existsByTelegramChatId(context.getChatId());
    }

    @Override
    public void check(ChatContext context) {
        if (!isSatisfiedBy(context)) {
            throw new ChatAlreadyExistsException(context.getChatId());
        }
    }
}
