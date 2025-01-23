package org.cedacri.pingpong.interfaces;

import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.enums.TournamentType;

import java.time.LocalDate;
import java.util.Set;

public interface ITournament {

    Integer getMaxPlayers();
    Integer getId();
    String getTournamentName();
    String getTournamentStatus();
    TournamentType getTournamentType();
    LocalDate getCreatedAt();
    Set<Player> getPlayers();

}

