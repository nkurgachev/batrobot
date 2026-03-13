package com.batrobot.bot.infrastructure.telegram.command.handler;

import com.batrobot.bot.infrastructure.config.LocaleOverrideProperties;
import com.batrobot.bot.infrastructure.telegram.command.dto.CommandEnvelope;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.CommandHandler;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandDocumentation;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandHandlerComponent;
import com.batrobot.bot.infrastructure.telegram.command.mapper.CommandRequestMapper;
import com.batrobot.bot.infrastructure.telegram.formatter.command.MeResultFormatter;
import com.batrobot.orchestration.application.dto.request.CommonRequest;
import com.batrobot.orchestration.application.dto.response.MeCommandResponse;
import com.batrobot.orchestration.application.usecase.query.GetChatUserRankHistoryQuery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handler for /me command.
 * Displays list of Steam accounts bound to the user within the chat.
 */
@Slf4j
@RequiredArgsConstructor
@CommandHandlerComponent("me")
@CommandDocumentation(descriptionKey = "me.doc.description", usage = "/me")
public class MeCommandHandler implements CommandHandler {

    private final GetChatUserRankHistoryQuery getPlayersWithRankHistoryQuery;
    private final MeResultFormatter formatter;
    private final CommandRequestMapper commonRequestMapper;
    private final LocaleOverrideProperties localeOverrides;

    @Override
    public String handle(CommandEnvelope envelope) {
        log.info("Handling me command for chat {}, user {}",
                envelope.payload().chat().telegramChatId(),
                envelope.payload().user().telegramUserId());

        CommonRequest request = commonRequestMapper.toCommonRequest(envelope.payload());
        MeCommandResponse response = getPlayersWithRankHistoryQuery.execute(request);
        return formatter.formatResult(response, resolveLanguageCode(envelope, localeOverrides.getOverrides()));
    }
}
