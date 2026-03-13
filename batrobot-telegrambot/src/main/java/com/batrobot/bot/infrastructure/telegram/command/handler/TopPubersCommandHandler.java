package com.batrobot.bot.infrastructure.telegram.command.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.batrobot.bot.infrastructure.config.LocaleOverrideProperties;
import com.batrobot.bot.infrastructure.telegram.command.dto.CommandEnvelope;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.CommandHandler;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandDocumentation;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandHandlerComponent;
import com.batrobot.bot.infrastructure.telegram.formatter.command.TopPubersResultFormatter;
import com.batrobot.orchestration.application.dto.response.TopPubersResponse;
import com.batrobot.orchestration.application.usecase.query.GetTopPubersForChatQuery;

/**
 * Handler for /top_pubers command.
 * Displays all players in the chat grouped and sorted by their seasonal rank.
 */
@Slf4j
@RequiredArgsConstructor
@CommandHandlerComponent("top_pubers")
@CommandDocumentation(descriptionKey = "top_pubers.doc.description", usage = "/top_pubers")
public class TopPubersCommandHandler implements CommandHandler {

    private final TopPubersResultFormatter formatter;
    private final GetTopPubersForChatQuery getTopPubersInChatQuery;
    private final LocaleOverrideProperties localeOverrides;

    @Override
    public String handle(CommandEnvelope envelope) {
        log.info("Handling top_pubers for chat {}",
                envelope.payload().chat().telegramChatId());

        Long chatId = envelope.payload().chat().telegramChatId();
        TopPubersResponse pubers = getTopPubersInChatQuery.execute(chatId);
        return formatter.formatPubers(pubers, resolveLanguageCode(envelope, localeOverrides.getOverrides()));
    }
}
