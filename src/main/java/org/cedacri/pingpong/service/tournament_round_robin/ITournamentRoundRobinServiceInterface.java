package org.cedacri.pingpong.service.tournament_round_robin;

import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.TournamentOlympic;
import org.cedacri.pingpong.entity.TournamentRoundRobin;
import org.cedacri.pingpong.service.ITournamentServiceInterface;

import java.util.List;

public interface ITournamentRoundRobinServiceInterface extends ITournamentServiceInterface {
    // Define methods specific to Round Robin tournaments if needed,
    // For example, methods to handle the unique rules or structure of Round Robin tournaments
    // These methods can be added here as needed, similar to how ITournamentServiceInterface is structured

    void validateTournamentForGeneration(TournamentRoundRobin tournamentRobinRound);
    void generateRobinRoundMatches(TournamentRoundRobin tournamentRobinRound);
    public List<Player> calculatePlayerRankings(TournamentRoundRobin tournamentRobinRound);
//    void distributePlayersInFirstRound(TournamentRoundRobin tournamentRoundRobin);
//    void handleWalkoverPlayers(TournamentRoundRobin tournamentRoundRobin);
}
