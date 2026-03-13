package com.batrobot.bot.infrastructure.telegram.command.handler;

import com.batrobot.bot.infrastructure.telegram.command.dto.CommandEnvelope;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.CommandHandler;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandDocumentation;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandHandlerComponent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@CommandHandlerComponent("awards_monthly")
@CommandDocumentation(descriptionKey = "award_monthly.doc.description", usage = "/awards_monthly")
public class AwardsMonthlyCommandHandler implements CommandHandler {

    @Override
    public String handle(CommandEnvelope envelope) {
        log.info("Handling awards_monthly command for chat {}",
                envelope.payload().chat().telegramChatId());

        return "Command is not implemented yet";
    }
}
