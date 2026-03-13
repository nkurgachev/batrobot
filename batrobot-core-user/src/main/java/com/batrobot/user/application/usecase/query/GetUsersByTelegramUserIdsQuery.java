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

import java.util.List;

/**
 * UseCase for getting Telegram users by their external Telegram IDs.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class GetUsersByTelegramUserIdsQuery {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Gets Telegram users by their Telegram IDs (external IDs).
     * 
     * @param telegramUserIds list of Telegram user ID values
     * @return list of user responses for found users
     */
    @Transactional(readOnly = true)
    public List<UserResponse> execute(@NotNull List<Long> telegramUserIds) {
        log.debug("Getting Telegram users by Telegram IDs: {}", telegramUserIds);

        return userRepository.findAllByTelegramUserId(telegramUserIds.stream()
                .map(TelegramUserId::of)
                .toList())
            .stream()
            .map(user -> {
                log.debug("Found user: telegramUserId={}", user.getTelegramUserId());
                return userMapper.toResponse(user);
            })
            .toList();
    }
}

