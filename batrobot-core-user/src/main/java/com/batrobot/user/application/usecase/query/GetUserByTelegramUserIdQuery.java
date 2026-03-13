package com.batrobot.user.application.usecase.query;

import com.batrobot.user.application.dto.response.UserResponse;
import com.batrobot.user.application.mapper.UserMapper;
import com.batrobot.user.domain.repository.UserRepository;
import com.batrobot.shared.domain.model.valueobject.TelegramUserId;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

/**
 * UseCase for getting a Telegram user by external Telegram ID.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class GetUserByTelegramUserIdQuery {

    private final UserRepository telegramUserRepository;
    private final UserMapper telegramUserMapper;

    /**
     * Gets a Telegram user by their Telegram ID (external ID).
     * 
     * @param telegramUserId Telegram user ID value
     * @return Optional with user response if found, empty otherwise
     */
    @Transactional(readOnly = true)
    public Optional<UserResponse> execute(@NotNull Long telegramUserId) {
        log.debug("Getting Telegram user by Telegram ID: {}", telegramUserId);

        return telegramUserRepository.findByTelegramUserId(TelegramUserId.of(telegramUserId))
            .map(user -> {
                log.debug("Found user: telegramUserId={}", telegramUserId);
                return telegramUserMapper.toResponse(user);
            });
    }
}

