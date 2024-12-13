package org.cedacri.pingpong.service;

import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.repository.PlayerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Stream<Player> list(long page) {
        return playerRepository.paged(page);
    }

    public Stream<Player> getAll() {
        return playerRepository.getAll();
    }

    @Transactional
    public Optional<Player> findById(Integer id) {
        return playerRepository.findById(id);
    }

    @Transactional
    public Player save(Player player) {
        return playerRepository.save(player);
    }

    @Modifying
    @Transactional
    public void deleteById(Integer id) {
        playerRepository.delete(id);
    }
}
