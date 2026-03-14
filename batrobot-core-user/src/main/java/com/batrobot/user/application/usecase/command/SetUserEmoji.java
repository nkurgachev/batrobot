package com.batrobot.user.application.usecase.command;

import com.batrobot.shared.domain.model.valueobject.TelegramUserId;
import com.batrobot.user.application.exception.UserNotFoundException;
import com.batrobot.user.application.dto.response.UserResponse;
import com.batrobot.user.application.mapper.UserMapper;
import com.batrobot.user.domain.repository.UserRepository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * Use case for updating a user's preferred emoji.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class SetUserEmoji {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Updates preferred emoji for an existing Telegram user.
     *
     * @param telegramUserId Telegram user ID
     * @param emoji new preferred emoji
     * @return updated user
     * @throws UserNotFoundException if user with given Telegram ID does not exist
     */
    @Transactional
    public UserResponse execute(@NotNull Long telegramUserId, @NotBlank String emoji) 
        throws UserNotFoundException{
        TelegramUserId id = TelegramUserId.of(telegramUserId);

        var user = userRepository.findByTelegramUserId(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (user.updateEmoji(emoji)) {
            userRepository.save(user);
            log.debug("Updated emoji for user {}", telegramUserId);
        }

        return userMapper.toResponse(user);
    }
}
