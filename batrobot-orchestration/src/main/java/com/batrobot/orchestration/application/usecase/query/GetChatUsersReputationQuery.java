package com.batrobot.orchestration.application.usecase.query;

import com.batrobot.binding.application.dto.response.ChatPlayerBindingResponse;
import com.batrobot.binding.application.usecase.query.GetBindingsByChatQuery;
import com.batrobot.chat.application.dto.response.ChatResponse;
import com.batrobot.chat.application.usecase.query.GetChatByTelegramChatIdQuery;
import com.batrobot.user.application.dto.response.UserResponse;
import com.batrobot.user.application.usecase.query.GetUsersByTelegramUserIdsQuery;

import com.batrobot.orchestration.application.dto.response.RepsCommandResponse;
import com.batrobot.orchestration.application.dto.response.RepsCommandResponse.UserReputation;
import com.batrobot.orchestration.application.exception.OrchestrationNoAccountsException;
import com.batrobot.orchestration.application.exception.OrchestrationRepsNoParticipantsException;
import com.batrobot.orchestration.application.mapper.OrchestrationResponseMapper;
import com.batrobot.orchestration.application.port.config.ReputationConfig;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Orchestration: fetches today's matches for given players.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Validated
public class GetChatUsersReputationQuery {

    private final GetBindingsByChatQuery getBindingsByChatQuery;
    private final GetUsersByTelegramUserIdsQuery getUsersByTelegramUserIdsQuery;
    private final GetChatByTelegramChatIdQuery getChatByTelegramChatIdQuery;
    private final OrchestrationResponseMapper responseMapper;
    private final ReputationConfig reputationConfig;

    /**
     * Fetches reputation for all users in the chat.
     * Each user can have multiple bound Steam accounts.
     *
     * @param chatId Telegram chat ID to fetch reputation for
     * @return list of user reputation responses with username and reputation score
     * @throws OrchestrationNoAccountsException if no accounts are linked to the chat
     * @throws OrchestrationRepsNoParticipantsException if no users are found for the chat
     */
    public RepsCommandResponse execute(@NotNull Long chatId) 
            throws OrchestrationNoAccountsException,
            OrchestrationRepsNoParticipantsException {
        log.debug("Executing GetUsersReputationQuery for chat {}", chatId);

        String chatTitle;

        // Step 1: Get all bindings for this chat
        List<ChatPlayerBindingResponse> bindings = getBindingsByChatQuery.execute(chatId);
        if (bindings.isEmpty()) {
            chatTitle = getChatByTelegramChatIdQuery.execute(chatId)
                .map(ChatResponse::title)
                .orElse("Unknown");
            throw new OrchestrationNoAccountsException(chatId, chatTitle);
        }

        // Step 2: Collect unique Telegram user IDs
        List<Long> uniqueUserIds = bindings.stream()
                .map(ChatPlayerBindingResponse::telegramUserId)
                .distinct()
                .toList();

        // Step 3: Fetch user information for these IDs
        List<UserResponse> users = getUsersByTelegramUserIdsQuery.execute(uniqueUserIds);
        if (users.isEmpty()) {
            throw new OrchestrationRepsNoParticipantsException(chatId);
        }

        // Step 4: Build result with reputation calculations using mapper
        List<UserReputation> reputations = users.stream()
                .map(user -> {
                    String username = user.username() != null ? user.username()
                            : (user.firstName() != null ? user.firstName() : "Unknown");
                    int reputation = resolveReputation(username);
                    log.debug("User {} has reputation {}", username, reputation);
                    return responseMapper.toUserReputation(user, reputation);
                })
                .sorted((a, b) -> Integer.compare(b.reputation(), a.reputation()))
                .toList();

        log.debug("Fetched reputation for {} users in chat {}", reputations.size(), chatId);
        return responseMapper.toRepsCommandResponse(reputations);
    }

    private int resolveReputation(String username) {
        Integer fixed = reputationConfig.getFixedReputations().get(username);
        if (fixed != null) {
            return fixed;
        }
        return ThreadLocalRandom.current().nextInt(-52, 53);
    }
}
