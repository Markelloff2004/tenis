package org.cedacri.pingpong.interfaces;

import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.enums.SetTypesEnum;
import org.cedacri.pingpong.enums.TournamentStatusEnum;
import org.cedacri.pingpong.enums.TournamentTypeEnum;

import java.time.LocalDate;
import java.util.Set;

public interface ITournament
{

    Long getId();

    String getTournamentName();

    TournamentStatusEnum getTournamentStatus();

    TournamentTypeEnum getTournamentType();

    Integer getMaxPlayers();

    Set<Player> getPlayers();

    SetTypesEnum getSetsToWin();

    Player getWinner();

    LocalDate getCreatedAt();

}

