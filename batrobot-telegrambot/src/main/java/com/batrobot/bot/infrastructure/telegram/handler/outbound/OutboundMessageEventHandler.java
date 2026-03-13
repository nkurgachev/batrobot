package com.batrobot.bot.infrastructure.telegram.handler.outbound;

import com.batrobot.bot.infrastructure.telegram.event.outbound.OutboundMessageRequestedEvent;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboundMessageEventHandler {

    private final TelegramBot bot;

    @EventListener
    public void onOutboundMessageRequested(OutboundMessageRequestedEvent event) {
        sendMessage(event.chatId(), event.messageId(), event.text());
    }

    private void sendMessage(Long chatId, Integer messageId, String text) {
        log.debug("Sending message to chat {}", chatId);

        SendMessage request = new SendMessage(chatId, text).parseMode(ParseMode.HTML);
        if (messageId != null) {
            request.replyToMessageId(messageId);
        }

        request.disableWebPagePreview(true);

        SendResponse response = bot.execute(request);
        if (response.isOk()) {
            log.trace("Message sent successfully");
            return;
        }

        log.error("Failed to send message: {}", response.description());
    }
}
