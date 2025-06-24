package org.cedacri.pingpong.service.players;

import org.cedacri.pingpong.model.player.Player;

import java.util.List;

public interface IPlayerService {
    Player createPlayer(Player player);
    void updatePlayerStats(Long playerId);
    List<Player> getRankedPlayers();

}
