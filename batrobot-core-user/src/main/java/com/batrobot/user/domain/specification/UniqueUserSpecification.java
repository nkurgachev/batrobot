package com.batrobot.user.domain.specification;

import com.batrobot.user.domain.exception.UserAlreadyExistsException;
import com.batrobot.user.domain.repository.UserRepository;
import com.batrobot.shared.domain.model.valueobject.TelegramUserId;
import com.batrobot.shared.domain.specification.Specification;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * Specification for ensuring that a player (identified by Steam ID) is not already existing.
 */
@RequiredArgsConstructor
public class UniqueUserSpecification implements
        Specification<UniqueUserSpecification.UserContext> {

    private final UserRepository userRepository;

    /**
     * Context for checking the specification.
     * Contains all necessary data for checking uniqueness.
     */
    @Value
    @Builder
    public static class UserContext {
        TelegramUserId userId;
    }

    @Override
    public boolean isSatisfiedBy(UserContext context) {
        return userRepository
                .existsByTelegramUserId(context.getUserId());
    }

    @Override
    public void check(UserContext context) {
        if (!isSatisfiedBy(context)) {
            throw new UserAlreadyExistsException(context.getUserId());
        }
    }
}
