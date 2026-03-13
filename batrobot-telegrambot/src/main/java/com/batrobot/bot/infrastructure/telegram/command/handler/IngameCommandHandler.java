package com.batrobot.bot.infrastructure.telegram.command.handler;

import com.batrobot.bot.infrastructure.config.LocaleOverrideProperties;
import com.batrobot.bot.infrastructure.telegram.command.dto.CommandEnvelope;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.CommandHandler;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandDocumentation;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandHandlerComponent;
import com.batrobot.bot.infrastructure.telegram.formatter.command.IngameResultFormatter;
import com.batrobot.orchestration.application.dto.response.InGameCommandResponse;
import com.batrobot.orchestration.application.usecase.query.GetChatUsersInGameQuery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handler for /ingame command.
 * Displays list of Steam players currently in-game from all bound accounts in
 * the chat.
 */
@Slf4j
@RequiredArgsConstructor
@CommandHandlerComponent("ingame")
@CommandDocumentation(descriptionKey = "ingame.doc.description", usage = "/ingame")
public class IngameCommandHandler implements CommandHandler {

    private final GetChatUsersInGameQuery getPlayersInGameQuery;
    private final IngameResultFormatter formatter;
    private final LocaleOverrideProperties localeOverrides;

    @Override
    public String handle(CommandEnvelope envelope) {
        log.info("Handling ingame command for chat {}",
                envelope.payload().chat().telegramChatId());

        Long chatId = envelope.payload().chat().telegramChatId();
        InGameCommandResponse response = getPlayersInGameQuery.execute(chatId);
        return formatter.formatResult(response, resolveLanguageCode(envelope, localeOverrides.getOverrides()));
    }
}
