package com.batrobot.bot.infrastructure.telegram.bot;

import com.batrobot.bot.infrastructure.config.TelegramBotProperties;
import com.batrobot.bot.infrastructure.telegram.event.inbound.InboundMessageReceivedEvent;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

import org.springframework.context.ApplicationEventPublisher;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

/**
 * Telegram bot implementation that listens for updates and handles messages.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class BatRobot {

    private final TelegramBot bot;
    private final TelegramBotProperties telegramBotProperties;
    private final ApplicationEventPublisher eventPublisher;

    private String botUsername;

    @PostConstruct
    public void init() {
        try {
            this.botUsername = telegramBotProperties.getUsername();
            this.bot.setUpdatesListener(updates -> {
                for (Update update : updates) {
                    onUpdateReceived(update);
                }
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            });

            log.info("Bot initialized with username: {}", this.botUsername);
        } catch (Exception e) {
            log.error("Failed to initialize bot with username: {}", this.botUsername, e);
            throw e;
        }
    }

    public void onUpdateReceived(Update update) {
        log.trace("Received update: {}", update);

        if (update.message() != null) {
            eventPublisher.publishEvent(
                new InboundMessageReceivedEvent(
                    update.message(),
                    botUsername
                )
            );
        } else {
            log.trace("Update does not contain a text message");
        }
    }
}
