package org.cedacri.pingpong.service.tournament_olympic;

import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.entity.TournamentOlympic;
import org.cedacri.pingpong.service.ITournamentServiceInterface;

import java.util.List;

public interface ITournamentOlympicServiceInterface extends ITournamentServiceInterface {
    // Define methods specific to Olympic tournaments if needed,
    // For example, methods to handle the unique rules or structure of Olympic tournaments
    // These methods can be added here as needed, similar to how ITournamentServiceInterface is structured

    void validateTournamentForGeneration(TournamentOlympic tournamentOlympic);
    void generateOlympicBracket(TournamentOlympic tournamentOlympic);
    void distributePlayersInFirstRound(TournamentOlympic tournamentOlympic);
    void handleWalkoverPlayers(TournamentOlympic tournamentOlympic);
}
