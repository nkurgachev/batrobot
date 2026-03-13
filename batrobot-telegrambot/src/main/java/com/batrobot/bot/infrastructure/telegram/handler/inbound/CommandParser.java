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

    private static final String COMMAND_REGEX_TEMPLATE = "^/([a-zA-Z0-9_]+)(?:@%s)?\\s*(.*)$";
    private static final int COMMAND_NAME_GROUP = 1;
    private static final int COMMAND_ARGS_GROUP = 2;
    private static final String ARGUMENTS_SPLIT_REGEX = "\\s+";

    private final InboundMappper inboundMapper;

    public Optional<CommandEnvelope> parseCommand(Message message, String botUsername) {
        String rawText = message.text();
        if (rawText == null || rawText.trim().isEmpty() || botUsername == null) {
            return Optional.empty();
        }

        String messageText = rawText.trim();
        Pattern pattern = buildCommandPattern(botUsername);
        Matcher matcher = pattern.matcher(messageText);

        if (!matcher.matches()) {
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

    private Pattern buildCommandPattern(String botUsername) {
        String escapedBotUsername = Pattern.quote(botUsername);
        String regex = String.format(COMMAND_REGEX_TEMPLATE, escapedBotUsername);
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }
}