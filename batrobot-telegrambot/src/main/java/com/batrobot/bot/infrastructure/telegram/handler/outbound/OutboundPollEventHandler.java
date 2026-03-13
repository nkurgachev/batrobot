package com.batrobot.bot.infrastructure.telegram.handler.outbound;

import com.batrobot.bot.infrastructure.telegram.event.outbound.OutboundPollRequestedEvent;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendPoll;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboundPollEventHandler {

    private final TelegramBot bot;

    @EventListener
    public void onOutboundPollRequested(OutboundPollRequestedEvent event) {
        sendPoll(event.chatId(), event.question(), event.options(), event.isAnonymous());
    }

    private void sendPoll(Long chatId, String question, List<String> options, boolean isAnonymous) {
        log.debug("Sending poll to chat {}", chatId);

        String[] optionsArray = options.toArray(String[]::new);
        SendPoll request = new SendPoll(chatId, question, optionsArray)
                .isAnonymous(isAnonymous);

        SendResponse response = bot.execute(request);
        if (response.isOk()) {
            log.trace("Poll sent successfully");
            return;
        }

        log.error("Failed to send poll: {}", response.description());
    }
}
