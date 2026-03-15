package com.batrobot.bot.infrastructure.telegram.handler.inbound;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.batrobot.bot.infrastructure.telegram.command.dto.CommandEnvelope;
import com.batrobot.bot.infrastructure.telegram.command.mapper.InboundMappper;
import com.pengrad.telegrambot.model.Message;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CommandParser {

    private static final Pattern COMMAND_PATTERN = Pattern.compile(
        "^/([a-zA-Z0-9_]+)(?:@([a-zA-Z0-9_]+))?\\s*(.*)$",
        Pattern.CASE_INSENSITIVE
    );
    private static final int COMMAND_NAME_GROUP = 1;
    private static final int COMMAND_BOT_USERNAME_GROUP = 2;
    private static final int COMMAND_ARGS_GROUP = 3;
    private static final String ARGUMENTS_SPLIT_REGEX = "\\s+";

    private final InboundMappper inboundMapper;

    public Optional<CommandEnvelope> parseCommand(Message message, String botUsername) {
        String rawText = message.text();
        if (rawText == null || rawText.trim().isEmpty() || botUsername == null) {
            return Optional.empty();
        }

        String messageText = rawText.trim();
        Matcher matcher = COMMAND_PATTERN.matcher(messageText);

        if (!matcher.matches()) {
            return Optional.empty();
        }

        String addressedBotUsername = matcher.group(COMMAND_BOT_USERNAME_GROUP);
        if (addressedBotUsername != null && !addressedBotUsername.equalsIgnoreCase(botUsername)) {
            return Optional.empty();
        }

        String commandName = matcher.group(COMMAND_NAME_GROUP).toLowerCase();
        String argsText = matcher.group(COMMAND_ARGS_GROUP).trim();
        String[] commandArgs = argsText.isEmpty() ? new String[0] : argsText.split(ARGUMENTS_SPLIT_REGEX);

        OffsetDateTime timestamp = OffsetDateTime.ofInstant(
            Instant.ofEpochSecond(message.date()),
            ZoneOffset.UTC
        );

        CommandEnvelope envelope = inboundMapper.toCommandEnvelope(
            message.chat(),
            message.from(),
            commandName,
            commandArgs,
            message.messageId(),
            message.from().languageCode(),
            timestamp
        );

        return Optional.of(envelope);
    }
}