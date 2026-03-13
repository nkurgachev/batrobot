package com.batrobot.user.application.usecase.command;

import com.batrobot.user.application.dto.request.UserRequest;
import com.batrobot.user.application.dto.response.UserResponse;
import com.batrobot.user.application.mapper.UserMapper;
import com.batrobot.user.domain.repository.UserRepository;
import com.batrobot.shared.domain.model.valueobject.TelegramUserId;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * UseCase for upserting a Telegram user.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class UpsertUser {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Upserts a Telegram user - creates if not exists, updates if info changed.
     * Single database operation using repository.
     * 
     * @param request User request with Telegram data
     * @return User response
     */
    @Transactional
    public UserResponse execute(@Valid UserRequest request) {
        log.debug("Upserting Telegram user: telegramUserId={}", request.getTelegramUserId());

        TelegramUserId telegramUserId = TelegramUserId.of(request.getTelegramUserId());

        return userRepository.findByTelegramUserId(telegramUserId)
            .map(existing -> {
                // User exists, check if info needs updating
                boolean hasChanges = false;

                hasChanges |= existing.updateUsername(request.getUsername());

                hasChanges |= existing.updateBio(
                    request.getFirstName(), 
                    request.getLastName()
                );
                
                if (hasChanges) {
                    userRepository.save(existing);
                    log.debug("Updated user info for user {}", telegramUserId.value());
                }
                return userMapper.toResponse(existing);
            })
            .orElseGet(() -> {
                // User doesn't exist, create new one
                log.debug("Creating new user: telegramUserId={}", telegramUserId.value());
                var newUser = userMapper.createFromRequest(request);
                var savedUser = userRepository.save(newUser);
                return userMapper.toResponse(savedUser);
            });
    }
}
