package com.batrobot.bot.infrastructure.telegram.command.mapper;

import com.batrobot.bot.infrastructure.telegram.command.exception.TelegramCommandInputException;

import java.text.BreakIterator;
import java.util.Locale;

/**
 * Shared helper for emoji argument parsing from telegram command arguments.
 */
public interface EmojiArgumentMapperSupport {

    String MESSAGE_KEY_NO_EMOJI = "common.exception.no_emoji";
    String MESSAGE_KEY_INVALID_EMOJI = "common.exception.invalid_emoji";
    default String parseEmoji(String[] args) {
        if (args == null || args.length == 0 || args[0] == null || args[0].isBlank()) {
            throw new TelegramCommandInputException(MESSAGE_KEY_NO_EMOJI);
        }

        String emoji = args[0].trim();

        if (emoji.chars().anyMatch(Character::isWhitespace)
                || countGraphemeClusters(emoji) != 1
                || !looksLikeEmoji(emoji)) {
            throw new TelegramCommandInputException(MESSAGE_KEY_INVALID_EMOJI, emoji);
        }

        return emoji;
    }

    private static int countGraphemeClusters(String value) {
        BreakIterator iterator = BreakIterator.getCharacterInstance(Locale.ROOT);
        iterator.setText(value);

        int count = 0;
        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            if (start < end) {
                count++;
            }
        }

        return count;
    }

    private static boolean looksLikeEmoji(String value) {
        boolean hasSymbol = false;

        for (int i = 0; i < value.length();) {
            int cp = value.codePointAt(i);
            i += Character.charCount(cp);

            int type = Character.getType(cp);
            boolean isEmojiLike = type == Character.OTHER_SYMBOL
                    || type == Character.NON_SPACING_MARK
                    || type == Character.ENCLOSING_MARK
                    || cp == 0x200D
                    || cp == 0xFE0F
                    || cp == 0xFE0E
                    || (cp >= 0x1F3FB && cp <= 0x1F3FF);

            if (!isEmojiLike) {
                return false;
            }

            if (type == Character.OTHER_SYMBOL) {
                hasSymbol = true;
            }
        }

        return hasSymbol;
    }
}