package com.batrobot.bot.infrastructure.telegram.command.handler;

import com.batrobot.bot.infrastructure.telegram.command.dto.CommandEnvelope;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.CommandHandler;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandDocumentation;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandHandlerComponent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@CommandHandlerComponent("stats_weekly")
@CommandDocumentation(descriptionKey = "stats_weekly.doc.description", usage = "/stats_weekly")
public class StatsWeeklyCommandHandler implements CommandHandler {

    @Override
    public String handle(CommandEnvelope envelope) {
        log.info("Handling stats_weekly command for chat {}",
                envelope.payload().chat().telegramChatId());

        return "Command is not implemented yet";
    }
}
