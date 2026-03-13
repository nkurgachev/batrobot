package com.batrobot.bot.infrastructure.telegram.command.handler;

import com.batrobot.bot.infrastructure.config.LocaleOverrideProperties;
import com.batrobot.bot.infrastructure.telegram.command.dto.CommandEnvelope;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.CommandHandler;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandDocumentation;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandHandlerComponent;
import com.batrobot.bot.infrastructure.telegram.formatter.command.RepsResultFormatter;
import com.batrobot.orchestration.application.dto.response.RepsCommandResponse;
import com.batrobot.orchestration.application.usecase.query.GetChatUsersReputationQuery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@CommandHandlerComponent("reps")
@CommandDocumentation(descriptionKey = "reps.doc.description", usage = "/reps", noteKey = "reps.doc.note")
public class RepsCommandHandler implements CommandHandler {

    private final GetChatUsersReputationQuery getUsersReputationQuery;
    private final RepsResultFormatter formatter;
    private final LocaleOverrideProperties localeOverrides;

    @Override
    public String handle(CommandEnvelope envelope) {
        log.info("Handling reps command for chat {}",
                envelope.payload().chat().telegramChatId());

        Long chatId = envelope.payload().chat().telegramChatId();
        RepsCommandResponse response = getUsersReputationQuery.execute(chatId);
        return formatter.formatReputation(response, resolveLanguageCode(envelope, localeOverrides.getOverrides()));
    }
}
