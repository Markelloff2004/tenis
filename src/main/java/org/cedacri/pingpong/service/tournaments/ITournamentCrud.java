package org.cedacri.pingpong.service.tournaments;

import org.cedacri.pingpong.model.tournament.BaseTournament;

import java.util.List;

public interface ITournamentCrud {
    List<BaseTournament> findAllTournaments();
    BaseTournament findTournamentById(Long id);
    BaseTournament createTournament(BaseTournament tournament);
    BaseTournament updateTournament(BaseTournament tournament);
    void deleteTournamentById(Long id);
}

