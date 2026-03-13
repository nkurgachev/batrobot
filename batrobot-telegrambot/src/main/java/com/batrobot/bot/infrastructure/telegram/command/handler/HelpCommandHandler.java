package com.batrobot.bot.infrastructure.telegram.command.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.batrobot.bot.infrastructure.config.LocaleOverrideProperties;
import com.batrobot.bot.infrastructure.telegram.command.dto.CommandEnvelope;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.CommandHandler;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandDocumentation;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandHandlerComponent;
import com.batrobot.bot.infrastructure.telegram.formatter.command.HelpResultFormatter;

@Slf4j
@RequiredArgsConstructor
@CommandHandlerComponent("help")
@CommandDocumentation(descriptionKey = "help.doc.description", usage = "/help")
public class HelpCommandHandler implements CommandHandler {

    private final HelpResultFormatter formatter;
    private final LocaleOverrideProperties localeOverrides;

    @Override
    public String handle(CommandEnvelope envelope) {
        log.info("Handling help command for chat {}",
                envelope.payload().chat().telegramChatId());
        return formatter.formatResult(resolveLanguageCode(envelope, localeOverrides.getOverrides()));
    }
}
