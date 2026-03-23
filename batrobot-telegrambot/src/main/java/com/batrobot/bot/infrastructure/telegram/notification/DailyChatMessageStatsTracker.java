package com.batrobot.bot.infrastructure.telegram.notification;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

/**
 * In-memory tracker for daily non-command message counts grouped by chat.
 */
@Component
public class DailyChatMessageStatsTracker {

    private final Map<Long, ChatStatsState> chatStats = new HashMap<>();

    public synchronized void increment(long chatId, String telegramUsername, String languageCode) {
        ChatStatsState state = chatStats.computeIfAbsent(chatId, ignored -> new ChatStatsState());
        int currentCount = state.messageCounts.getOrDefault(telegramUsername, 0);
        state.messageCounts.put(telegramUsername, currentCount + 1);

        if (languageCode != null && !languageCode.isBlank()) {
            state.languageCode = languageCode;
        }
    }

    public synchronized Optional<ChatMessageStatsSnapshot> peekForChat(long chatId) {
        ChatStatsState state = chatStats.get(chatId);
        if (state == null || state.messageCounts.isEmpty()) {
            return Optional.empty();
        }

        LinkedHashMap<String, Integer> sortedCounts = state.messageCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .collect(LinkedHashMap::new,
                        (map, item) -> map.put(item.getKey(), item.getValue()),
                        LinkedHashMap::putAll);

        return Optional.of(new ChatMessageStatsSnapshot(chatId, state.languageCode, sortedCounts));
    }

    public synchronized List<ChatMessageStatsSnapshot> drainAll() {
        List<ChatMessageStatsSnapshot> snapshots = new ArrayList<>(chatStats.size());

        for (Map.Entry<Long, ChatStatsState> entry : chatStats.entrySet()) {
            if (entry.getValue().messageCounts.isEmpty()) {
                continue;
            }

            LinkedHashMap<String, Integer> sortedCounts = entry.getValue().messageCounts.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                            .thenComparing(Map.Entry.comparingByKey()))
                    .collect(LinkedHashMap::new,
                            (map, item) -> map.put(item.getKey(), item.getValue()),
                            LinkedHashMap::putAll);

            snapshots.add(new ChatMessageStatsSnapshot(entry.getKey(), entry.getValue().languageCode, sortedCounts));
        }

        chatStats.clear();
        return snapshots;
    }

    private static final class ChatStatsState {
        private final Map<String, Integer> messageCounts = new HashMap<>();
        private String languageCode;
    }

    public record ChatMessageStatsSnapshot(
            long chatId,
            String languageCode,
            Map<String, Integer> messageCounts) {
    }
}