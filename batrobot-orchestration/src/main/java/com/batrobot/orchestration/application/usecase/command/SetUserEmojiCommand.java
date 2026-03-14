package com.batrobot.orchestration.application.usecase.command;

import com.batrobot.orchestration.application.dto.request.SetEmojiCommandRequest;
import com.batrobot.orchestration.application.dto.response.SetEmojiCommandResponse;
import com.batrobot.orchestration.application.exception.OrchestrationUserNotFoundException;
import com.batrobot.orchestration.application.mapper.OrchestrationResponseMapper;
import com.batrobot.user.application.exception.UserNotFoundException;
import com.batrobot.user.application.usecase.command.SetUserEmoji;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Orchestration use case for updating user emoji.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class SetUserEmojiCommand {

    private final SetUserEmoji setUserEmoji;
    private final OrchestrationResponseMapper responseMapper;

    /**
     * Updates preferred emoji for Telegram user.
     *
     * @param request command request with user context and emoji
     * @return response with updated emoji
     */
    public SetEmojiCommandResponse execute(@Valid SetEmojiCommandRequest request) {
        Long telegramUserId = request.getUser().getTelegramUserId();

        log.debug("Updating emoji for telegram user {}", telegramUserId);

        try {
            return responseMapper.toSetEmojiCommandResponse(setUserEmoji.execute(telegramUserId, request.getEmoji()));
        } catch (UserNotFoundException e) {
            throw new OrchestrationUserNotFoundException(e.getTelegramUserId().value());
        }
    }
}
