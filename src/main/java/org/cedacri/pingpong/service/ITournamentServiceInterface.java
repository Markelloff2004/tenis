package org.cedacri.pingpong.service;

import org.cedacri.pingpong.entity.BaseTournament;

import java.util.List;

public interface ITournamentServiceInterface {

    // Define methods that will be implemented by the TournamentService class

    List<BaseTournament> findAllTournaments();
    BaseTournament findTournamentById(Long id);
    void deleteTournamentById(Long id);
    BaseTournament startTournament(BaseTournament tournament);

    void generateMatches();

//        BaseTournament createTournament(BaseTournament tournament);
}
