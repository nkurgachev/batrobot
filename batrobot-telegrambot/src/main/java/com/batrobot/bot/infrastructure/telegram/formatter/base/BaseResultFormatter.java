package com.batrobot.bot.infrastructure.telegram.formatter.base;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import org.springframework.context.MessageSource;

/**
 * Base formatter with common locale resolution logic.
 */
public abstract class BaseResultFormatter {

    protected Locale resolveLocale(String languageCode) {
        return Optional.ofNullable(languageCode)
                .map(Locale::forLanguageTag)
                .orElse(Locale.getDefault());
    }

    // Telegram supported HTML tags
    protected static String bold(String text) {
        return "<b>" + escapeHtml(text) + "</b>";
    }

    protected static String italic(String text) {
        return "<i>" + escapeHtml(text) + "</i>";
    }

    protected static String underline(String text) {
        return "<u>" + escapeHtml(text) + "</u>";
    }

    protected static String strike(String text) {
        return "<s>" + escapeHtml(text) + "</s>";
    }

    protected static String spoiler(String text) {
        return "<tg-spoiler>" + escapeHtml(text) + "</tg-spoiler>";
    }

    protected static String blockquote(String text) {
        return "<blockquote>" + escapeHtml(text) + "</blockquote>";
    }

    protected static String link(String text, String url) {
        return "<a href=\"" + escapeHtmlAttribute(url) + "\">" + escapeHtml(text) + "</a>";
    }

    protected static String monospace(String text) {
        return "<code>" + escapeHtml(text) + "</code>";
    }

    protected static String mention(String username) {
        return "@" + escapeHtml(username);
    }

    protected static String pre(String text) {
        return "<pre>" + escapeHtml(text) + "</pre>";
    }

    protected static String pre(String text, String language) {
        return "<pre><code class=\"language-" + escapeHtmlAttribute(language) + "\">"
                + escapeHtml(text)
                + "</code></pre>";
    }

    protected static String formatFullName(String firstName, String lastName, String fallback) {
        String first = firstName != null ? firstName : "";
        String last = lastName != null ? lastName : "";
        String fullName = (first + " " + last).trim();
        return fullName.isEmpty() ? fallback : fullName;
    }

    protected static String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private static String escapeHtmlAttribute(String text) {
        return escapeHtml(text).replace("'", "&#39;");
    }

    // other formating methods
    protected static String formatAward(String award) {
        return switch (award) {
            case "MVP" -> "💜";
            case "TOP_CORE" -> "❤️";
            case "TOP_SUPPORT" -> "🩵";
            case "BEST_PLAY" -> "💛"; //TODO: проверить по БД
            default -> award;
        };
    }

    protected static String formatGameMode(String gameMode) {
        return switch (gameMode) {
            case "ABILITY_DRAFT" -> "AD";
            case "ALL_PICK_RANKED" -> "RANKED";
            case "ALL_PICK" -> "AP";
            case "ALL_RANDOM_DEATH_MATCH" -> "ARDM";
            case "CAPTAINS_MODE" -> "CM";
            case "CAPTAINS_DRAFT" -> "CD";
            case "RANDOM_DRAFT" -> "RD";
            case "SINGLE_DRAFT" -> "SD";
            case "ALL_RANDOM" -> "AR";
            case "TURBO" -> "TURBO";
            default -> gameMode;
        };
    }

    protected static String formatPosition(String position) {
        return switch (position) {
            case "POSITION_1" -> "1️⃣";
            case "POSITION_2" -> "2️⃣";
            case "POSITION_3" -> "3️⃣";
            case "POSITION_4" -> "4️⃣";
            case "POSITION_5" -> "5️⃣";
            default -> position;
        };
    }

    protected static String formatLobbyType(String lobbyType) {
        return switch (lobbyType) {
            case "RANKED" -> "RANKED";
            case "UNRANKED" -> "UNRANKED";
            case "BATTLE_CUP" -> "BC";
            default -> lobbyType;
        };
    }

    protected static String formatResult(Boolean isVictory) {
        if (isVictory == null) {
            return "Unknown";
        }
        return isVictory ? "🟢" : "🔴";
    }

    protected static String formatDateTime(Long epochSeconds) {
        return epochSeconds != null ?
            Instant.ofEpochSecond(epochSeconds)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("HH:mm"))
            : "Unknown Time";
    }

    protected static String formatImp(Integer imp) {
        return switch (imp) {
            case null -> "";
            default -> {
                if (imp > 0) {
                    yield "🟪 " + imp;
                } else if (imp < 0) {
                    yield "⬜️ " + imp;
                } else {
                    yield "";
                }
            }
        };
    }

    protected static String formatRankAsMedal(Integer rank, MessageSource messageSource, Locale locale) {
        if (rank == null) {
            return messageSource.getMessage("format.match.medal.0", null, locale);
        }

        int medal = rank / 10;
        int tier = rank % 10;

        String medalLabel = messageSource.getMessage("format.match.medal." + medal, null, locale);

        if (tier > 0) {
            return medalLabel + " " + toRomanNumeral(tier);
        }

        return medalLabel;
    }

    private static String toRomanNumeral(int number) {
        return switch (number) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> String.valueOf(number);
        };
    }

    protected static String formatReputation(Integer reputation) {
        return switch (reputation) {
            case null -> "";
            case 52 -> "🟪 " + reputation;
            default -> {
                if (reputation > 0) {
                    yield "🟩 " + reputation;
                } else if (reputation < 0) {
                    yield "🟥 " + reputation;
                } else {
                    yield "";
                }
            }
        };        
    }
}
