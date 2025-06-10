package org.cedacri.pingpong.service.composed_services;

import org.cedacri.pingpong.entity.BaseTournament;
import org.cedacri.pingpong.entity.Player;
import org.cedacri.pingpong.service.PlayerService;
import org.cedacri.pingpong.service.TournamentService;

public class TournamentPlayerService {

    private final TournamentService tournamentService;
    private final PlayerService playerService;

    public TournamentPlayerService(TournamentService tournamentService, PlayerService playerService) {
        this.tournamentService = tournamentService;
        this.playerService = playerService;
    }

    // Add methods to handle tournament and player interactions

    public void addPlayerIntoTournament(Player playerToAdd, BaseTournament tournament) {
        if (playerToAdd == null || tournament == null) {
            throw new IllegalArgumentException("Player and Tournament cannot be null");
        }

        if (!tournamentService.isTournamentActive(tournament)) {
            throw new IllegalStateException("Cannot add player to an inactive tournament");
        }

        if (isPlayerRegisteredInTournament(playerToAdd, tournament)) {
            throw new IllegalStateException("Player is already registered in the tournament");
        }

        tournament.addPlayer(playerToAdd);
        playerToAdd.registerToTournament(tournament);

        tournamentService.saveTournament(tournament);
        playerService.savePlayer(playerToAdd);
    }

    private boolean isPlayerRegisteredInTournament(Player player, BaseTournament tournament) {
        return tournament.getPlayers().contains(player);
    }
}
