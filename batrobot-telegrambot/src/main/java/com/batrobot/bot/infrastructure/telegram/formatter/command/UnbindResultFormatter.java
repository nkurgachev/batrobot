package com.batrobot.bot.infrastructure.telegram.formatter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.batrobot.bot.infrastructure.telegram.formatter.base.BaseResultFormatter;
import com.batrobot.orchestration.application.dto.response.UnbindCommandResponse;

import java.util.Locale;

/**
 * Formatter for unbind command results.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UnbindResultFormatter extends BaseResultFormatter {

    private final static String MESSAGE_KEY_UNBIND_SUCCESS = "unbind.success.binding_removed";
    private final MessageSource messageSource;

    /**
     * Formats successful unbinding result into a user-friendly message with HTML formatting.
     *
     * @param removedBinding the removed Steam binding
     * @param telegramUsername Telegram username of the user
     * @param languageCode telegram language code
     * @return formatted HTML message string
     */
    public String formatSuccess(UnbindCommandResponse removedBinding, String languageCode) {
        Locale locale = resolveLocale(languageCode);
        log.debug("Formatting unbind success for Steam account {}", removedBinding.steamId64());
        
        String steamAccountName = removedBinding.steamUsername() != null ? 
            bold(removedBinding.steamUsername()) : bold("Unknown");
        String telegramUsernameEscaped = removedBinding.telegramUsername() != null ? 
            escapeHtml(removedBinding.telegramUsername()) : "Unknown";
        
        String message = messageSource.getMessage(
                MESSAGE_KEY_UNBIND_SUCCESS,
                new Object[]{
                    steamAccountName,
                    telegramUsernameEscaped
                },
                locale
        );
        
        log.debug("Successfully formatted unbind success message");
        return message;
    }
    
}
