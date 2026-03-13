package com.batrobot.bot.infrastructure.telegram.command.handler;

import com.batrobot.bot.infrastructure.config.LocaleOverrideProperties;
import com.batrobot.bot.infrastructure.telegram.command.dto.CommandEnvelope;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.CommandHandler;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandDocumentation;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandHandlerComponent;
import com.batrobot.bot.infrastructure.telegram.command.mapper.CommandRequestMapper;
import com.batrobot.bot.infrastructure.telegram.formatter.command.UnbindResultFormatter;
import com.batrobot.orchestration.application.dto.request.UnbindCommandRequest;
import com.batrobot.orchestration.application.dto.response.UnbindCommandResponse;
import com.batrobot.orchestration.application.usecase.command.UnbindPlayerFromChatUserCommand;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handler for /unbind command.
 */
@Slf4j
@RequiredArgsConstructor
@CommandHandlerComponent("unbind")
@CommandDocumentation(descriptionKey = "unbind.doc.description", usage = "/unbind steamId", example = "/unbind 76561198012345678")
public class UnbindCommandHandler implements CommandHandler {

    private final UnbindPlayerFromChatUserCommand deleteSteamBindingCommand;
    private final UnbindResultFormatter formatter;
    private final CommandRequestMapper commandRequestMapper;
    private final LocaleOverrideProperties localeOverrides;

    @Override
    public String handle(CommandEnvelope envelope) {
        log.info("Handling unbind command for chat {}, user {}",
                envelope.payload().chat().telegramChatId(),
                envelope.payload().user().telegramUserId());

        UnbindCommandRequest request = commandRequestMapper.toUnbindCommandRequest(envelope.payload());
        UnbindCommandResponse removedBinding = deleteSteamBindingCommand.execute(request);
        return formatter.formatSuccess(removedBinding, resolveLanguageCode(envelope, localeOverrides.getOverrides()));
    }
}
