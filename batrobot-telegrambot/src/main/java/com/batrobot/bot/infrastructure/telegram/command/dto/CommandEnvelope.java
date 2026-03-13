package com.batrobot.bot.infrastructure.telegram.command.dto;

import java.time.OffsetDateTime;

/**
 * Envelope with command payload and transport metadata.
 */
public record CommandEnvelope(
        Payload payload,
        Metadata metadata) {

    public record Payload(
            Chat chat,
            User user,
            Command command) {

        public record Chat(
                Long telegramChatId,
                String type,
                String title) {
        }

        public record User(
                Long telegramUserId,
                String username,
                String firstName,
                String lastName) {
        }

        public record Command(
                String name,
                String[] arguments) {
        }
    }

    public record Metadata(
            Integer messageId,
            String languageCode,
            OffsetDateTime timestamp) {
    }
}
