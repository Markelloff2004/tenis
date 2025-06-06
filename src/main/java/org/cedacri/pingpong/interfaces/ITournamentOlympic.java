package org.cedacri.pingpong.interfaces;

import org.cedacri.pingpong.enums.SetTypesEnum;

public interface ITournamentOlympic extends ITournament {

    SetTypesEnum getSetsToWin();

    SetTypesEnum getSemifinalsSetsToWin();

    SetTypesEnum getFinalsSetsToWin();
}
