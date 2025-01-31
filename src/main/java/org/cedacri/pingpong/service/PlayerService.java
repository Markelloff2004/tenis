package org.cedacri.pingpong.service;

import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.repository.PlayerRepository;
import org.cedacri.pingpong.utils.Constraints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private static final Logger logger = LoggerFactory.getLogger(PlayerService.class);

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player findById(Long id) {
        return playerRepository.findById(id).orElseThrow();
    }
    public Stream<Player> list(long page) {
        logger.info("Fetching list of players for page {}", page);

        Pageable pageable = PageRequest.of((int) page, Constraints.PAGE_SIZE);

        Page<Player> playerPage = playerRepository.findAll(pageable);

        return playerPage.stream();
    }

    public Stream<Player> getAll() {
        logger.info("Fetching list of players");
        return playerRepository.findAll().stream();
    }

    @Transactional
    public void save(Player player) {
        if (player == null) {
            logger.error("Attempted to save a null Player");
            throw new IllegalArgumentException("Player cannot be null");
        }

        logger.debug("Attempting to save player {}", player);

        playerRepository.save(player);
        logger.debug("Successfully saved player {}", player);
    }

    @Modifying
    @Transactional
    public void deleteById(Long id) {
        if (id != null){
            logger.debug("Attempting to delete player with id {}", id);

            playerRepository.deleteById(id);
            logger.debug("Successfully deleted player with id {}", id);
        }
        else
        {
            logger.error("Attempting to delete player with null id");
            throw new IllegalArgumentException("Player Id cannot be null");
        }
    }
}
