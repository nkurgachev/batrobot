package com.batrobot.user.application.usecase.command;

import com.batrobot.user.application.dto.request.UserRequest;
import com.batrobot.user.application.dto.response.UserResponse;
import com.batrobot.user.application.mapper.UserMapper;
import com.batrobot.user.domain.exception.UserAlreadyExistsException;
import com.batrobot.user.domain.model.User;
import com.batrobot.user.domain.repository.UserRepository;
import com.batrobot.user.domain.specification.UniqueUserSpecification;
import com.batrobot.user.domain.specification.UniqueUserSpecification.UserContext;
import com.batrobot.shared.domain.model.valueobject.TelegramUserId;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * UseCase for creating a new Telegram user.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class CreateUser {

    private final UserRepository telegramUserRepository;
    private final UserMapper telegramUserMapper;
    private final UniqueUserSpecification uniqueUserSpecification;

    /**
     * Creates a new Telegram user.
     * 
     * @param request User data from Telegram
     * @return Created user response
     * @throws UserAlreadyExistsException if user with this Telegram ID already
     *                                    exists
     */
    @Transactional
    public UserResponse execute(@Valid UserRequest request)
            throws UserAlreadyExistsException {
        log.debug("Creating new Telegram user: telegramUserId={}", request.getTelegramUserId());

        TelegramUserId telegramUserId = TelegramUserId.of(request.getTelegramUserId());

        uniqueUserSpecification.check(UserContext.builder()
                .userId(telegramUserId)
                .build());

        User newUser = telegramUserMapper.createFromRequest(request);
        User savedUser = telegramUserRepository.save(newUser);

        log.info("Successfully created new Telegram user: {}", telegramUserId.value());

        return telegramUserMapper.toResponse(savedUser);
    }
}

