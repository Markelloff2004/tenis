package org.cedacri.pingpong.service;

import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public Optional<Player> getPlayerById(Long id) {
        return playerRepository.findById(id);
    }

    public Player createPlayer(Player player) {
        return playerRepository.save(player);
    }

    public Player updatePlayer(Long id, Player playerDetails) {
        return playerRepository.findById(id).map(player -> {
            player.setName(playerDetails.getName());
            player.setRank(playerDetails.getRank());
            player.setAge(playerDetails.getAge());
            player.setStyle(playerDetails.getStyle());
            return playerRepository.save(player);
        }).orElseThrow(() -> new RuntimeException("Player not found with id " + id));
    }

    public void deletePlayer(Player playerToDelete) {
        playerRepository.findById(playerToDelete.getId()).ifPresent(player -> {playerRepository.delete(player);});
    }
}
