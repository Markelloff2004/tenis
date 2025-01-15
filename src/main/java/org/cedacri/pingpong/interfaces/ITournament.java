package org.cedacri.pingpong.interfaces;

import org.cedacri.pingpong.entity.Player;

import java.time.Instant;
import java.util.Set;

public interface ITournament {

    Integer getMaxPlayers();
    Integer getId();
    String getTournamentName();
    String getTournamentStatus();
    String getTournamentType();
    Instant getCreatedAt();
    Set<Player> getPlayers();

}

