package org.cedacri.pingpong.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.exception.tournament.EntityDeletionException;
import org.cedacri.pingpong.repository.PlayerRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;
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

    public Stream<Player> getAll() {
        log.info("Fetching list of players");

        List<Player> players = playerRepository.findAll();

        return players.stream();
    }

    @Transactional
    public Player save(@Valid Player player) {
        log.debug("Attempting to save player {}", player);

        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }

        if (player.getId() != null && playerRepository.findById(player.getId()).isEmpty()) {
                throw new IllegalArgumentException("Cannot update non-existing player with ID: " + player.getId());
            }


        Player savedPlayer = playerRepository.save(player);

        log.debug("Successfully saved player {}", player);
        return savedPlayer;
    }

    @Modifying
    @Transactional
    public void deleteById(Long id) {
        if (id == null) {
            log.error("Attempting to delete a player with null ID");
            throw new IllegalArgumentException("Player ID cannot be null.");
        }

        log.debug("Attempting to delete player with ID {}", id);

        if (!playerRepository.existsById(id)) {
            log.warn("Player with ID {} does not exist in the database.", id);
            throw new EntityNotFoundException("Player with ID " + id + " does not exist and cannot be deleted.");
        }

        try {
            playerRepository.deleteById(id);
            playerRepository.flush();
            log.info("Successfully deleted player with ID {}", id);
        } catch (DataIntegrityViolationException ex) {
            log.info("Cannot delete player with ID {} due to foreign key constraints, as it affects tournament statistics", id);
            throw new EntityDeletionException("Player with ID " + id + " cannot be deleted because they are associated with one or more tournaments.", ex);
        } catch (Exception ex) {
            log.error("Unexpected error while deleting player with ID {}", id, ex);
            throw new IllegalStateException("An unexpected error occurred while deleting the player with ID " + id, ex);

        }
    }

}