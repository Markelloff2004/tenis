package org.cedacri.pingpong.interfaces;

import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.enums.SetTypesEnum;
import org.cedacri.pingpong.enums.TournamentStatusEnum;
import org.cedacri.pingpong.enums.TournamentTypeEnum;

import java.time.LocalDate;
import java.util.Set;

public interface ITournament {

    Integer getMaxPlayers();
    Integer getId();
    String getTournamentName();
    SetTypesEnum getSetsToWin();
    SetTypesEnum getSemifinalsSetsToWin();
    SetTypesEnum getFinalsSetsToWin();
    TournamentStatusEnum getTournamentStatus();
    TournamentTypeEnum getTournamentType();
    LocalDate getCreatedAt();
    Set<Player> getPlayers();

}

