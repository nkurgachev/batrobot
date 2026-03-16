package com.batrobot.bot.infrastructure.telegram.handler.inbound;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;

import com.batrobot.bot.infrastructure.config.LocaleOverrideProperties;
import com.batrobot.bot.infrastructure.telegram.command.dto.CommandEnvelope;
import com.batrobot.bot.infrastructure.telegram.command.exception.TelegramCommandInputException;
import com.batrobot.bot.infrastructure.telegram.event.inbound.InboundMessageReceivedEvent;
import com.batrobot.bot.infrastructure.telegram.event.outbound.OutboundMessageRequestedEvent;
import com.batrobot.bot.infrastructure.telegram.notification.DailyChatMessageStatsTracker;
import com.batrobot.orchestration.application.exception.base.OrchestrationCommandException;
import com.batrobot.shared.application.exception.ApplicationException;
import com.batrobot.shared.domain.exception.DomainException;

import com.pengrad.telegrambot.model.Message;

import jakarta.validation.ConstraintViolationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;
import java.util.Optional;

/**
 * Central handler for incoming Telegram messages.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class InboundMessageEventHandler {

    private final ApplicationEventPublisher eventPublisher;

    private final CommandHandlerFactory handlerFactory;
    private final CommandParser commandParser;
    private final DailyChatMessageStatsTracker dailyChatMessageStatsTracker;
    private final LocaleOverrideProperties localeOverrides;

    private final MessageSource messageSource;

    @EventListener
    public void onInboundMessageReceived(InboundMessageReceivedEvent event) {
        Message message = event.message();
        String botUsername = event.botUsername();

        log.debug("Processing message from user {} in chat {}", message.from().username(), message.chat().id());

        commandParser.parseCommand(message, botUsername)
                .ifPresentOrElse(
                        this::handleCommand,
                        () -> handleNonCommand(message));
    }

    private void handleCommand(CommandEnvelope envelope) {
        log.info("Command {} detected", envelope.payload().command());

        handlerFactory.getHandler(envelope.payload().command().name())
                .ifPresentOrElse(
                        handler -> {
                            try {
                                String responseText = handler.handle(envelope);
                                sendMessage(envelope, responseText);
                            } catch (TelegramCommandInputException e) {
                                sendExpectedError(envelope, e.getMessageKey(), e.getMessageArgs(), e);
                            } catch (OrchestrationCommandException e) {
                                sendExpectedError(envelope, e.getMessageKey(), e.getMessageArgs(), e);
                            } catch (ConstraintViolationException e) {
                                sendExpectedError(envelope, "common.exception.invalid_input", null, e);
                            } catch (ApplicationException | DomainException e) {
                                sendUnexpectedError(envelope, "common.exception.unexpected_error", e);
                            } catch (Exception e) {
                                sendUnexpectedError(envelope, "common.exception.unexpected_error", e);
                            }
                        },
                        () -> log.debug("Ignoring unsupported command: {}", envelope.payload().command().name()));
    }

    private void sendMessage(CommandEnvelope envelope, String text) {
        OutboundMessageRequestedEvent response = new OutboundMessageRequestedEvent(
                envelope.payload().chat().telegramChatId(),
                envelope.metadata().messageId(),
                text);
        eventPublisher.publishEvent(response);
    }

    private void sendExpectedError(CommandEnvelope envelope, String messageKey, Object[] args,
            Exception exception) {
        log.warn("Command processing failed with key {}: {}", messageKey, exception.getMessage());

        Locale locale = getLocale(envelope.metadata().languageCode());
        String text = messageSource.getMessage(
                messageKey,
                args,
                locale);
        sendMessage(envelope, text);
    }

    private void sendUnexpectedError(CommandEnvelope envelope, String messageKey,
            Exception exception) {
        log.warn("Command processing failed with key {}: {}", messageKey, exception.getMessage(), exception);

        Locale locale = getLocale(envelope.metadata().languageCode());
        String text = messageSource.getMessage(
                messageKey,
                null,
                locale);
        sendMessage(envelope, text);
    }

    private Locale getLocale(String languageCode) {
        return Optional.ofNullable(languageCode)
                .filter(code -> !code.isBlank())
                .map(Locale::forLanguageTag)
                .orElse(localeOverrides.notificationLocale());
    }

    private void handleNonCommand(Message message) {
        if (message == null || message.text() == null || message.text().isBlank()) {
            return;
        }

        if (message.chat() == null || message.from() == null) {
            log.debug("Skipping non-command message without chat or user context");
            return;
        }

        String username = message.from().username();
        if (username.isBlank()) {
            log.debug("Skipping non-command message from user without telegram username: userId={}", message.from().id());
            return;
        }

        String languageCode = resolveLanguageCode(username, message.from().languageCode());
        dailyChatMessageStatsTracker.increment(message.chat().id(), username, languageCode);
    }

    private String resolveLanguageCode(String username, String fallbackLanguageCode) {
        if (localeOverrides.getOverrides().containsKey(username)) {
            return localeOverrides.getOverrides().get(username);
        }
        return fallbackLanguageCode;
    }
}
