package org.cedacri.pingpong.views.interfaces;

import org.cedacri.pingpong.entity.BaseTournament;

public interface TournamentManagement
{
    void showCreateTournament();

    void showInfoTournament(BaseTournament tournamentOlympicDetails);

    void showEditTournament(BaseTournament tournamentOlympic);

    void showDeleteTournament(BaseTournament tournamentOlympicDelete);
}

