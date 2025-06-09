package org.cedacri.pingpong.views.interfaces;

import org.cedacri.pingpong.entity.TournamentOlympic;

public interface TournamentManagement
{
    void showCreateTournament();

    void showInfoTournament(TournamentOlympic tournamentOlympic);

    void showEditTournament(TournamentOlympic tournamentOlympic);

    void showDeleteTournament(TournamentOlympic tournamentOlympic);
}

