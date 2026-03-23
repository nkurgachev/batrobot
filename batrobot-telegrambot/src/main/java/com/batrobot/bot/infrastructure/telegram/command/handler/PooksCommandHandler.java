package com.batrobot.bot.infrastructure.telegram.command.handler;

import java.util.Optional;

import org.springframework.context.MessageSource;

import com.batrobot.bot.infrastructure.config.LocaleOverrideProperties;
import com.batrobot.bot.infrastructure.telegram.command.dto.CommandEnvelope;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.CommandHandler;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandDocumentation;
import com.batrobot.bot.infrastructure.telegram.command.handler.base.annotation.CommandHandlerComponent;
import com.batrobot.bot.infrastructure.telegram.formatter.base.BaseResultFormatter;
import com.batrobot.bot.infrastructure.telegram.formatter.notification.DailyChatMessageStatsFormatter;
import com.batrobot.bot.infrastructure.telegram.notification.DailyChatMessageStatsTracker;
import com.batrobot.bot.infrastructure.telegram.notification.DailyChatMessageStatsTracker.ChatMessageStatsSnapshot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@CommandHandlerComponent("pooks")
@CommandDocumentation(descriptionKey = "pooks.doc.description", usage = "/pooks")
public class PooksCommandHandler extends BaseResultFormatter implements CommandHandler {

    private static final String MESSAGE_KEY_NO_MESSAGES = "pooks.exception.no_messages";

    private final DailyChatMessageStatsTracker tracker;
    private final DailyChatMessageStatsFormatter formatter;
    private final MessageSource messageSource;
    private final LocaleOverrideProperties localeOverrides;

    @Override
    public String handle(CommandEnvelope envelope) {
        long chatId = envelope.payload().chat().telegramChatId();
        log.info("Handling pooks command for chat {}", chatId);

        String languageCode = resolveLanguageCode(envelope, localeOverrides.getOverrides());

        Optional<ChatMessageStatsSnapshot> snapshot = tracker.peekForChat(chatId);
        if (snapshot.isEmpty()) {
            return messageSource.getMessage(MESSAGE_KEY_NO_MESSAGES, null, resolveLocale(languageCode));
        }

        return formatter.format(snapshot.get().messageCounts(), snapshot.get().languageCode());
    }
}
