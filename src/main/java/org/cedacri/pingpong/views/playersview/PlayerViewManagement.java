package org.cedacri.pingpong.views.playersview;

import org.cedacri.pingpong.model.player.Player;

public interface PlayerViewManagement {
    void showAllPlayers();

    void showCreatePlayer();

    void showDetailsPlayer(Player player);

    void showEditPlayer(Player player);

    void showDeletePlayer(Player player);
}
