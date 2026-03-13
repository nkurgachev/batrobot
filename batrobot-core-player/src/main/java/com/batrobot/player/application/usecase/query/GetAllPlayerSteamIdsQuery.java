package com.batrobot.player.application.usecase.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.batrobot.player.domain.model.Player;
import com.batrobot.player.domain.repository.PlayerRepository;

import java.util.List;

/**
 * Query Use Case for getting all player Steam IDs, ordered by updatedAt.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GetAllPlayerSteamIdsQuery {

    private final PlayerRepository playerRepository;

    /**
     * Returns all player Steam ID 64-bit values, ordered by updatedAt ascending.
     *
     * @return List of Steam ID 64-bit values
     */
    @Transactional(readOnly = true)
    public List<Long> execute() {
        List<Player> players = playerRepository.findAllByOrderByUpdatedAt();

        log.debug("Found {} players for update", players.size());

        return players.stream()
                .map(player -> player.getSteamId().value())
                .toList();
    }
}

