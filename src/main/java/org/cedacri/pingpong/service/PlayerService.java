package org.cedacri.pingpong.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.repository.PlayerRepository;
import org.cedacri.pingpong.utils.Constants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Validated
@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player findById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Player with id {} not found", id);
                    return new IllegalArgumentException("Player not found");
                });
    }

//    public Stream<Player> list(long page) {
//        log.info("Fetching list of players for page {}", page);
//
//        Pageable pageable = PageRequest.of((int) page, Constants.PAGE_SIZE);
//
//        Page<Player> playerPage = playerRepository.findAll(pageable);
//
//        return playerPage.stream();
//    }

    public Stream<Player> getAll() {
        log.info("Fetching list of players");

        List<Player> players = playerRepository.findAll();

        if (players == null) {
            players = Collections.emptyList();
        }

        return players.stream();
    }

    @Transactional
    public Player save(@Valid Player player) {
        log.debug("Attempting to save player {}", player);

        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }

        if (player.getId() != null) {
            if (playerRepository.findById(player.getId()).isEmpty()) {
                throw new IllegalArgumentException("Cannot update non-existing player with ID: " + player.getId());
            }
        }

        Player savedPlayer = playerRepository.save(player);

        log.debug("Successfully saved player {}", player);
        return savedPlayer;
    }

    @Modifying
    @Transactional
    public void deleteById(Long id) {
        if (id != null) {
            log.debug("Attempting to delete player with id {}", id);

            if (playerRepository.existsById(id)) {
                playerRepository.deleteById(id);
                log.debug("Successfully deleted player with id {}", id);
            } else {
                throw new EntityNotFoundException("Cannot delete a player that doesn't exist in database");
            }
        } else {
            log.error("Attempting to delete player with null id");
            throw new IllegalArgumentException("Player Id cannot be null");
        }
    }

//    public Set<Player> getAvailablePlayersForTournament(Tournament tournament) {
//        return playerRepository.findAll().stream()
//                .filter(p -> !p.getTournaments().contains(tournament))
//                .collect(Collectors.toSet());
//    }
}
