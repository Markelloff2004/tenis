package org.cedacri.pingpong.service.tournaments;

import org.cedacri.pingpong.model.tournament.BaseTournament;

import java.util.List;

public interface ITournamentCrud<T extends BaseTournament> {
    List<BaseTournament> findAllTournaments();
    T findTournamentById(Long id);
    T createTournament(T tournament);
    T updateTournament(T tournament);
    void deleteTournamentById(Long id);
}

