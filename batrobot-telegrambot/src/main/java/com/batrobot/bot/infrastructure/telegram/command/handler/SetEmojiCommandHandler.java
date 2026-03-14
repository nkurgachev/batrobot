package com.batrobot.bot.infrastructure.telegram.command.handler;

import com.batrobot.bot.infrastructure.config.LocaleOverrideProperties;
import com.batrobot.bot.infrastructure.telegram.command.dto.CommandEnvelope;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.CommandHandler;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandDocumentation;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandHandlerComponent;
import com.batrobot.bot.infrastructure.telegram.command.mapper.CommandRequestMapper;
import com.batrobot.bot.infrastructure.telegram.formatter.command.SetEmojiResultFormatter;
import com.batrobot.orchestration.application.dto.request.SetEmojiCommandRequest;
import com.batrobot.orchestration.application.dto.response.SetEmojiCommandResponse;
import com.batrobot.orchestration.application.usecase.command.SetUserEmojiCommand;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handler for /set_emoji command.
 */
@Slf4j
@RequiredArgsConstructor
@CommandHandlerComponent("set_emoji")
@CommandDocumentation(descriptionKey = "set_emoji.doc.description", usage = "/set_emoji emoji", example = "/set_emoji 😎")
public class SetEmojiCommandHandler implements CommandHandler {

    private final SetUserEmojiCommand setUserEmojiCommand;
    private final SetEmojiResultFormatter formatter;
    private final CommandRequestMapper commandRequestMapper;
    private final LocaleOverrideProperties localeOverrides;

    @Override
    public String handle(CommandEnvelope envelope) {
        log.info("Handling set_emoji command for chat {}, user {}",
                envelope.payload().chat().telegramChatId(),
                envelope.payload().user().telegramUserId());

        SetEmojiCommandRequest request = commandRequestMapper.toSetEmojiCommandRequest(envelope.payload());
        SetEmojiCommandResponse response = setUserEmojiCommand.execute(request);

        return formatter.formatResult(response, resolveLanguageCode(envelope, localeOverrides.getOverrides()));
    }
}
