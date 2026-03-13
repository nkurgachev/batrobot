package com.batrobot.bot.infrastructure.telegram.command.handler;

import com.batrobot.bot.infrastructure.config.LocaleOverrideProperties;
import com.batrobot.bot.infrastructure.telegram.command.dto.CommandEnvelope;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.CommandHandler;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandDocumentation;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandHandlerComponent;
import com.batrobot.bot.infrastructure.telegram.formatter.command.AllPubsTodayResultFormatter;
import com.batrobot.orchestration.application.dto.response.AllPubsTodayCommandResponse;
import com.batrobot.orchestration.application.usecase.query.GetAllPubsTodayForChatQuery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handler for /all_pubs_today command.
 * Displays today's match statistics for all bound accounts in the chat.
 */
@Slf4j
@RequiredArgsConstructor
@CommandHandlerComponent("all_pubs_today")
@CommandDocumentation(descriptionKey = "all_pubs_today.doc.description", usage = "/all_pubs_today")
public class AllPubsTodayCommandHandler implements CommandHandler {

    private final GetAllPubsTodayForChatQuery getAllPubsTodayForChatQuery;
    private final AllPubsTodayResultFormatter formatter;
    private final LocaleOverrideProperties localeOverrides;

    @Override
    public String handle(CommandEnvelope envelope) {        
        log.info("Handling all_pubs_today command for chat {}",
                envelope.payload().chat().telegramChatId());

        Long chatId = envelope.payload().chat().telegramChatId();
        AllPubsTodayCommandResponse response = getAllPubsTodayForChatQuery.execute(chatId);
        return formatter.formatResult(response, resolveLanguageCode(envelope, localeOverrides.getOverrides()));
    }
}
