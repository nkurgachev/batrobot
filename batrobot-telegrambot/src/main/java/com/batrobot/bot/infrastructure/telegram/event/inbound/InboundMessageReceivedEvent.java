package com.batrobot.bot.infrastructure.telegram.event.inbound;

import com.pengrad.telegrambot.model.Message;

/**
 * Infrastructure event for inbound Telegram messages.
 */
public record InboundMessageReceivedEvent(
    Message message,
    String botUsername) {
}