package org.cedacri.pingpong.service.tournaments;

import org.cedacri.pingpong.model.player.Player;
import org.cedacri.pingpong.model.tournament.BaseTournament;

public interface ITournamentOperations extends ITournamentCrud {

    void validateTournament(BaseTournament tournament);
    BaseTournament startTournament(BaseTournament tournament);
    void generateMatches(BaseTournament tournament);
    boolean allMatchesHasBeenPlayed(BaseTournament tournament);
    Player determineTournamentWinner(BaseTournament tournament);
    void endTournament(BaseTournament tournament);
}