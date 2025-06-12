package org.cedacri.pingpong.service.tournaments;

import org.cedacri.pingpong.model.player.Player;
import org.cedacri.pingpong.model.tournament.BaseTournament;

public interface ITournamentOperations<T extends BaseTournament> {

    void validateTournament(T tournament);
    T startTournament(T tournament);
    void generateMatches(T tournament);
    boolean allMatchesHasBeenPlayed(T tournament);
    Player determineTournamentWinner(T tournament);
    void endTournament(T tournament);
}