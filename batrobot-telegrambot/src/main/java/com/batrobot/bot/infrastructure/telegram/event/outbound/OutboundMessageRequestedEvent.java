package com.batrobot.bot.infrastructure.telegram.event.outbound;

/**
 * Infrastructure event for outbound Telegram messages.
 */
public record OutboundMessageRequestedEvent(
    Long chatId,
    Integer messageId,
    String text) {
}
