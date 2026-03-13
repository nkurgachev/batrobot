package com.batrobot.bot.infrastructure.telegram.command.handler;

import com.batrobot.bot.infrastructure.config.LocaleOverrideProperties;
import com.batrobot.bot.infrastructure.telegram.command.dto.CommandEnvelope;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.CommandHandler;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandDocumentation;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandHandlerComponent;
import com.batrobot.bot.infrastructure.telegram.command.mapper.CommandRequestMapper;
import com.batrobot.bot.infrastructure.telegram.formatter.command.BindResultFormatter;
import com.batrobot.orchestration.application.dto.request.BindCommandRequest;
import com.batrobot.orchestration.application.dto.response.BindCommandResponse;
import com.batrobot.orchestration.application.usecase.command.BindPlayerToChatUserCommand;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handler for /bind command.
 */
@Slf4j
@RequiredArgsConstructor
@CommandHandlerComponent("bind")
@CommandDocumentation(descriptionKey = "bind.doc.description", usage = "/bind steamId", example = "/bind 76561198012345678", noteKey = "bind.doc.note")
public class BindCommandHandler implements CommandHandler {

    private final BindPlayerToChatUserCommand bindPlayerToUserInChat;
    private final BindResultFormatter formatter;
    private final CommandRequestMapper commandRequestMapper;
    private final LocaleOverrideProperties localeOverrides;

    @Override
    public String handle(CommandEnvelope envelope) {
        log.info("Handling bind command for chat {}, user {}",
                envelope.payload().chat().telegramChatId(),
                envelope.payload().user().telegramUserId());

        BindCommandRequest request = commandRequestMapper.toBindCommandRequest(envelope.payload());
        BindCommandResponse response = bindPlayerToUserInChat.execute(request);
        return formatter.formatResult(response, resolveLanguageCode(envelope, localeOverrides.getOverrides()));
    }
}
