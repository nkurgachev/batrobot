package com.batrobot.bot.infrastructure.telegram.command.handler;

import com.batrobot.bot.infrastructure.config.LocaleOverrideProperties;
import com.batrobot.bot.infrastructure.telegram.command.dto.CommandEnvelope;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.CommandHandler;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandDocumentation;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandHandlerComponent;
import com.batrobot.bot.infrastructure.telegram.command.mapper.CommandRequestMapper;
import com.batrobot.bot.infrastructure.telegram.formatter.command.StatsPeriodResultFormatter;
import com.batrobot.orchestration.application.dto.request.CommonRequest;
import com.batrobot.orchestration.application.dto.response.StatsPeriodCommandResponse;
import com.batrobot.orchestration.application.usecase.query.GetUserStatsForPeriodQuery;
import com.batrobot.shared.application.port.config.AppDayTimeConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.time.ZoneId;

@Slf4j
@RequiredArgsConstructor
@CommandHandlerComponent("stats_weekly")
@CommandDocumentation(descriptionKey = "stats_weekly.doc.description", usage = "/stats_weekly")
public class StatsWeeklyCommandHandler implements CommandHandler {

    private static final String HEADER_KEY = "stats_weekly.template.header";

    private final GetUserStatsForPeriodQuery getUserStatsForPeriodQuery;
    private final StatsPeriodResultFormatter formatter;
    private final CommandRequestMapper commonRequestMapper;
    private final LocaleOverrideProperties localeOverrides;
    private final AppDayTimeConfig dayTimeConfig;

    @Override
    public String handle(CommandEnvelope envelope) {
        log.info("Handling stats_weekly command for chat {}, user {}",
                envelope.payload().chat().telegramChatId(),
                envelope.payload().user().telegramUserId());

        ZoneId zoneId = ZoneId.of(dayTimeConfig.getTimezone());
        long periodStart = OffsetDateTime.now(zoneId).minusDays(7).toEpochSecond();

        CommonRequest request = commonRequestMapper.toCommonRequest(envelope.payload());
        StatsPeriodCommandResponse response = getUserStatsForPeriodQuery.execute(request, periodStart);
        return formatter.formatResult(
            response,
            resolveLanguageCode(envelope, localeOverrides.getOverrides()),
            HEADER_KEY);
    }
}