package com.batrobot.binding.domain.specification;

import com.batrobot.binding.domain.exception.PlayerAlreadyBoundException;
import com.batrobot.binding.domain.repository.ChatPlayerBindingRepository;
import com.batrobot.shared.domain.model.valueobject.SteamId;
import com.batrobot.shared.domain.model.valueobject.TelegramChatId;
import com.batrobot.shared.domain.model.valueobject.TelegramUserId;
import com.batrobot.shared.domain.specification.Specification;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * Specification for ensuring that a Steam account is unique within a chat.
 */
@RequiredArgsConstructor
public class UniquePlayerBindingSpecification implements
        Specification<UniquePlayerBindingSpecification.BindingContext> {

    private final ChatPlayerBindingRepository bindingRepository;

    /**
     * Context for checking the specification.
     * Contains all necessary data for checking uniqueness.
     */
    @Value
    @Builder
    public static class BindingContext {
        TelegramChatId telegramChatId;
        TelegramUserId telegramUserId;
        SteamId steamId;
    }

    @Override
    public boolean isSatisfiedBy(BindingContext context) {
        return bindingRepository
                .findBindingInChatBySteamId(context.getTelegramChatId(), context.getSteamId())
                .isEmpty();
    }

    @Override
    public void check(BindingContext context) {
        bindingRepository
                .findBindingInChatBySteamId(context.getTelegramChatId(), context.getSteamId())
                .ifPresent(binding -> {
                    throw new PlayerAlreadyBoundException(binding.getUserId());
                });
    }
}
