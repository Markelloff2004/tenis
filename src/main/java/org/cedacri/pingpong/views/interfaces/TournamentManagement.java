package org.cedacri.pingpong.views.interfaces;

import org.cedacri.pingpong.entity.Tournament;

public interface TournamentManagement
{
    void showCreateTournament();

    void showInfoTournament(Tournament tournament);

    void showEditTournament(Tournament tournament);

    void showDeleteTournament(Tournament tournament);
}

