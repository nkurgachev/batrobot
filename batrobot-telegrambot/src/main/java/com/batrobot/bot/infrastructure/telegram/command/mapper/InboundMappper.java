package com.batrobot.bot.infrastructure.telegram.command.mapper;

import com.batrobot.bot.infrastructure.telegram.command.dto.CommandEnvelope;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;

import java.time.OffsetDateTime;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * Unified mapper for Telegram objects.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InboundMappper {

    /**
     * Maps Telegram Chat (from API) to CommandEnvelopeDto.Payload.Chat.
     * 
     * @param chat Telegram API entity
     * @return CommandEnvelopeDto.Payload.Chat
     */
    @Mapping(target = "telegramChatId", expression = "java(chat.id())")
    @Mapping(target = "type", expression = "java(chat.type() != null ? chat.type().name() : null)")
    @Mapping(target = "title", expression = "java(chat.title())")
    CommandEnvelope.Payload.Chat toPayloadChat(Chat chat);

    /**
     * Maps Telegram User (from API) to CommandEnvelopeDto.Payload.User.
     * 
     * @param user Telegram API entity
     * @return CommandEnvelopeDto.Payload.User
     */
    @Mapping(target = "telegramUserId", expression = "java(user.id())")
    @Mapping(target = "username", expression = "java(user.username())")
    @Mapping(target = "firstName", expression = "java(user.firstName())")
    @Mapping(target = "lastName", expression = "java(user.lastName())")
    CommandEnvelope.Payload.User toPayloadUser(User user);

    /**
     * Maps Telegram User (from API) to CommandEnvelopeDto.Payload.Command.
     * 
     * @param name Command name
     * @param arguments Command arguments
     * @return CommandEnvelopeDto.Payload.Command
     */
    CommandEnvelope.Payload.Command toPayloadCommand(String name, String[] arguments);

    /**
     * Maps Telegram Chat, User, command name and arguments to CommandEnvelopeDto.Payload.
     * @param chat Telegram API entity
     * @param user Telegram API entity
     * @param commandName Command name
     * @param commandArgs Command arguments
     * @return CommandEnvelopeDto.Payload
     */
    @Mapping(target = "command", expression = "java(toPayloadCommand(commandName, commandArgs))")
    CommandEnvelope.Payload toPayload(Chat chat, User user, String commandName, String[] commandArgs);

    /**
     * Maps Telegram message metadata to CommandEnvelopeDto.Metadata.
     * @param messageId Telegram message ID
     * @param languageCode Telegram message language code
     * @param timestamp Telegram message timestamp
     * @return CommandEnvelopeDto.Metadata
     */
    CommandEnvelope.Metadata toMetadata(Integer messageId, String languageCode, OffsetDateTime timestamp);

    /**
     * Maps Telegram Chat, User, command name and arguments along with message metadata to CommandEnvelopeDto.
     * @param chat Telegram API entity
     * @param user Telegram API entity
     * @param commandName Command name
     * @param commandArgs Command arguments
     * @param messageId Telegram message ID
     * @param languageCode Telegram message language code
     * @param timestamp Telegram message timestamp
     * @return CommandEnvelopeDto
     */
    @Mapping(target = "payload", expression = "java(toPayload(chat, user, commandName, commandArgs))")
    @Mapping(target = "metadata", expression = "java(toMetadata(messageId, languageCode, timestamp))")
    CommandEnvelope toCommandEnvelope(Chat chat, User user, String commandName, String[] commandArgs, Integer messageId, String languageCode, OffsetDateTime timestamp);
}
