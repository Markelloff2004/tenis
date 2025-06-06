package org.cedacri.pingpong.interfaces;

import org.cedacri.pingpong.entity.Tournament;
import org.cedacri.pingpong.entity.TournamentOlympic;

public interface IMatchGenerator<T extends Tournament> {

    /**
     * Generates matches for the given tournament.
     *
     * @param tournamentRef the reference to the tournament for which matches are to be generated
     */
    public void generateMatches(T tournamentRef);

    void distributePlayersInMatches(TournamentOlympic tournament);

    void validateTournament(TournamentOlympic tournament);

    void generateMatchStructure(TournamentOlympic tournament, int totalRounds);
}
