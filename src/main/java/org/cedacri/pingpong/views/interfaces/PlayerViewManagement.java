package org.cedacri.pingpong.views.interfaces;

import org.cedacri.pingpong.entity.Player;

public interface PlayerViewManagement
{
    void showAllPlayers();

    void showCreatePlayer();

    void showDetailsPlayer(Player player);

    void showEditPlayer(Player player);

    void showDeletePlayer(Player player);
}
