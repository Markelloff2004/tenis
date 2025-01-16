package org.cedacri.pingpong.service;

import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public Stream<Player> list(long page) {
        logger.info("Fetching list of players for page {}", page);
        return playerRepository.paged(page);
    }

    public Stream<Player> getAll() {
        logger.info("Fetching list of players");
        return playerRepository.getAll();
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
    public void deleteById(Integer id) {
        if (id != null){
            logger.debug("Attempting to delete player with id {}", id);

            playerRepository.delete(id);
            logger.debug("Successfully deleted player with id {}", id);
        }
        else
        {
            logger.error("Attempting to delete player with null id");
            throw new IllegalArgumentException("Player Id cannot be null");
        }
    }
}
