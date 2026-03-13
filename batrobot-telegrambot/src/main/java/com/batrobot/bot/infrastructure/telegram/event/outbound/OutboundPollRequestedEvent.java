package com.batrobot.bot.infrastructure.telegram.event.outbound;

import java.util.List;

/**
 * Infrastructure event for outbound Telegram polls.
 */
public record OutboundPollRequestedEvent(
    Long chatId,
    String question,
    List<String> options,
    boolean isAnonymous) {
}
